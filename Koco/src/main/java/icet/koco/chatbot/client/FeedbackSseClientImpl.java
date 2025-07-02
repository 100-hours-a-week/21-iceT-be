package icet.koco.chatbot.client;

import icet.koco.chatbot.dto.feedback.FeedbackAnswerRequestDto;
import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import icet.koco.chatbot.emitter.ChatEmitterRepository;
import icet.koco.chatbot.entity.ChatRecord.Role;
import icet.koco.chatbot.entity.ChatSession;
import icet.koco.chatbot.repository.ChatSessionRepository;
import icet.koco.chatbot.service.ChatRecordService;
import icet.koco.enums.ErrorMessage;
import icet.koco.global.exception.ResourceNotFoundException;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class FeedbackSseClientImpl implements FeedbackSseClient {

	private final ChatEmitterRepository chatEmitterRepository;
	private final ChatSessionRepository chatSessionRepository;
	private final ChatRecordService chatRecordService;

	private final WebClient webClient = WebClient.builder()
		.baseUrl("${AI_BASE_URL}")	// TODO: 실제 AI 서버 주소로 바꿔야 함
		.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
		.build();

	@Override
	public SseEmitter startFeedbackSession(FeedbackStartRequestDto requestDto) {
		SseEmitter emitter = new SseEmitter(0L); // 무제한 SSE
		chatEmitterRepository.save(requestDto.getSessionId(), emitter);

		StringBuilder fullResponse = new StringBuilder();

		webClient.post()
			.uri("/api/ai/v2/feedback/start")
			.bodyValue(requestDto)
			.accept(MediaType.TEXT_EVENT_STREAM)
			.retrieve()
			.bodyToFlux(String.class)
			.timeout(Duration.ofSeconds(60))
			.onErrorResume(e -> {
				emitter.completeWithError(e);
				return Flux.empty();
			})
			.doOnNext(data -> {
				try {
					if (data.startsWith("data: ")) {
						String content = data.substring(6); // "data: " 제거

						// 에러 메시지 처리
						if (content.startsWith("[ERROR]")) {
							emitter.send(SseEmitter.event().name("error").data(content));
							emitter.completeWithError(new RuntimeException(content));
							return;
						}

						emitter.send(SseEmitter.event().name("message").data(content));
						fullResponse.append(content).append("\n");
					}
				} catch (IOException e) {
					emitter.completeWithError(e);
				}
			})
			.doOnComplete(() -> {
				emitter.complete();

				// ChatSession 조회
				ChatSession chatSession = chatSessionRepository.findById(requestDto.getSessionId())
					.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CHAT_SESSION_NOT_FOUND));

				// ChatRecord 저장
				chatRecordService.save(chatSession, Role.assistant, fullResponse.toString().trim());
			})
			.subscribe();

		return emitter;
	}

	@Override
	public SseEmitter streamFollowupFeedback(FeedbackAnswerRequestDto requestDto) {
		SseEmitter emitter = chatEmitterRepository.findBySessionId(requestDto.getSessionId());
		if (emitter == null) {
			throw new IllegalStateException("SSE 연결이 존재하지 않습니다.");
		}

		StringBuilder fullResponse = new StringBuilder();

		webClient.post()
			.uri("/api/ai/v2/feedback/answer")
			.bodyValue(requestDto)
			.accept(MediaType.TEXT_EVENT_STREAM)
			.retrieve()
			.bodyToFlux(String.class)
			.timeout(Duration.ofSeconds(60))
			.onErrorResume(e -> {
				emitter.completeWithError(e);
				return Flux.empty();
			})
			.doOnNext(data -> {
				try {
					if (data.startsWith("data: ")) {
						String content = data.substring(6); // "data: " 제거

						// 에러 메시지 처리
						if (content.startsWith("[ERROR]")) {
							emitter.send(SseEmitter.event().name("error").data(content));
							emitter.completeWithError(new RuntimeException(content));
							return;
						}

						emitter.send(SseEmitter.event().name("message").data(content));
						fullResponse.append(content).append("\n");
					}
				} catch (IOException e) {
					emitter.completeWithError(e);
				}
			})
			.doOnComplete(() -> {
				emitter.complete();

				// ChatSession 조회
				ChatSession chatSession = chatSessionRepository.findById(requestDto.getSessionId())
					.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CHAT_SESSION_NOT_FOUND));

				// ChatRecord 저장
				chatRecordService.save(chatSession, Role.assistant, fullResponse.toString().trim());
			})
			.subscribe();

		return emitter;
	}
}
