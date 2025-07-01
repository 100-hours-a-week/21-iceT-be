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
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FeedbackSseClientImpl implements FeedbackSseClient {

	private final ChatEmitterRepository chatEmitterRepository;
	private final ChatSessionRepository chatSessionRepository;
	private final ChatRecordService chatRecordService;

	private final WebClient webClient = WebClient.builder()
		.baseUrl("http://ai-server-host") // TODO: 실제 호스트로 교체
		.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
		.build();

	@Override
	public SseEmitter streamStartFeedback(FeedbackStartRequestDto requestDto) {
		SseEmitter emitter = new SseEmitter(0L); // 무한 SSE
		chatEmitterRepository.save(requestDto.getSessionId(), emitter);

		StringBuilder fullResponse = new StringBuilder();

		webClient.post()
			.uri("/api/ai/v2/feedback/start")
			.bodyValue(requestDto)
			.accept(MediaType.TEXT_EVENT_STREAM)
			.retrieve()
			.bodyToFlux(String.class)
			.timeout(Duration.ofSeconds(60))
			.doOnNext(data -> {
				if (!data.startsWith("[ERROR]")) {
					try {
						emitter.send(SseEmitter.event().name("message").data(data));
						fullResponse.append(data).append("\n");
					} catch (IOException e) {
						emitter.completeWithError(e);
					}
				}
			})
			.doOnError(e -> {
				emitter.completeWithError(e);
			})
			.doOnComplete(() -> {
				emitter.complete();

				// ChatRecord 저장
				ChatSession chatSession = chatSessionRepository.findById(requestDto.getSessionId())
					.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CHAT_SESSION_NOT_FOUND));

				chatRecordService.save(chatSession, Role.assistant, fullResponse.toString().trim());
			})
			.subscribe();

		return emitter;
	}

	@Override
	public SseEmitter streamAnswer(FeedbackAnswerRequestDto requestDto) {
		SseEmitter emitter = chatEmitterRepository.findBySessionId(requestDto.getSessionId());
		if (emitter == null) {
			throw new IllegalStateException("SSE 연결이 존재하지 않습니다.");
		}

		webClient.post()
			.uri("/api/v1/feedback/answer")
			.bodyValue(requestDto)
			.accept(MediaType.TEXT_EVENT_STREAM)
			.retrieve()
			.bodyToFlux(String.class)
			.doOnNext(data -> {
				try {
					emitter.send(SseEmitter.event().name("message").data(data));
				} catch (IOException e) {
					emitter.completeWithError(e);
				}
			})
			.doOnError(emitter::completeWithError)
			.doOnComplete(() -> {
				// 총평 판단 등의 처리 가능
			})
			.subscribe();

		return emitter;
	}

}
