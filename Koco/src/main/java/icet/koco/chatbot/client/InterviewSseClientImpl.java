package icet.koco.chatbot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import icet.koco.chatbot.dto.ai.ChatbotStartRequestDto;
import icet.koco.chatbot.emitter.ChatEmitterRepository;
import icet.koco.chatbot.entity.ChatRecord.Role;
import icet.koco.chatbot.entity.ChatSession;
import icet.koco.chatbot.repository.ChatSessionRepository;
import icet.koco.chatbot.service.ChatRecordService;
import icet.koco.enums.ErrorMessage;
import icet.koco.global.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;


@Component
@Primary
@Slf4j
@RequiredArgsConstructor
public class InterviewSseClientImpl implements InterviewSseClient {

	@Value("${AI_BASE_URL}")
	private String baseUrl;

	private final ChatEmitterRepository chatEmitterRepository;
	private final ChatSessionRepository chatSessionRepository;
	private final ChatRecordService chatRecordService;

	private WebClient webClient;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@PostConstruct
	public void initWebClient() {
		log.info(">>> AI_BASE_URL 로드됨: {}", baseUrl);

		this.webClient = WebClient.builder()
			.baseUrl(baseUrl)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}

	/**
	 * 인터뷰 세션 시작
	 * @param requestDto
	 * @return
	 */
	@Override
	public SseEmitter startInterviewSession(ChatbotStartRequestDto requestDto) {
		SseEmitter emitter = new SseEmitter(0L);
		chatEmitterRepository.save(requestDto.getSessionId(), emitter);

		try {
			log.info(">>> [AI 요청] /api/ai/v2/interview/start 전송 내용:\n{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestDto));
		} catch (Exception e) {
			log.warn("requestDto 직렬화 실패", e);
		}


		StringBuilder fullResponse = new StringBuilder();

		webClient.post()
			.uri("/api/ai/v2/interview/start")
			.bodyValue(requestDto)
			.accept(MediaType.TEXT_EVENT_STREAM)
			.retrieve()
			.onStatus(status -> status.is5xxServerError(), response -> {
				// AI 서버 내부 오류 발생 시 로그 출력
				return response.bodyToMono(String.class)
					.doOnNext(errorBody -> {
						log.error("AI 서버 500 응답 수신: {}", errorBody);
					})
					.thenReturn(new RuntimeException("AI 서버 오류: 500"));
			})
			.onStatus(status -> status.is4xxClientError(), response -> {
				return response.bodyToMono(String.class)
					.doOnNext(errorBody -> {
						log.warn("AI 서버 4xx 응답 수신: {}", errorBody);
					})
					.thenReturn(new RuntimeException("AI 서버 요청 오류: 4xx"));
			})
			.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
			.filter(event -> event.data() != null)
			.timeout(Duration.ofSeconds(60))
			.onErrorResume(e -> {
				log.error("SSE 통신 중 에러 발생: {}", e.getMessage(), e);
				emitter.completeWithError(e);
				return Flux.empty();
			})
			.doOnNext(event -> {
				String raw = event.data();

				String data = raw;
				if (data == null) {
					log.info("SSE 데이터 null");
					return;
				}

				if (data.startsWith("data: ")) {
					data = data.substring(6);
				}

				if (data.startsWith("[ERROR]")) {
					log.warn("SSE 응답 오류 감지: {}", data);
					try {
						emitter.send(SseEmitter.event().name("error").data(data));
					} catch (IOException e) {
						emitter.completeWithError(e);
						return;
					}
					emitter.completeWithError(new RuntimeException(data));
				} else {
					log.info("SSE 응답 수신: {}", data);
					try {
						emitter.send(SseEmitter.event().name("message").data(data));
						fullResponse.append(data).append("\n");
					} catch (IOException e) {
						emitter.completeWithError(e);
					}
				}
			})
			.doOnComplete(() -> {
				log.info("SSE 응답 종료, 전체 응답 저장");
				emitter.complete();
				ChatSession chatSession = chatSessionRepository.findById(requestDto.getSessionId())
					.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CHAT_SESSION_NOT_FOUND));
				chatRecordService.save(chatSession, Role.assistant, fullResponse.toString().trim());
			})
			.subscribe();


		return emitter;

	}

//	@Override
//	public SseEmitter streamFollowupInterview(ChatbotFollowupRequestDto requestDto) {
//
//	};

}
