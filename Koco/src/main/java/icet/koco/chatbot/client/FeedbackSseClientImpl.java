package icet.koco.chatbot.client;

import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
public class FeedbackSseClientImpl implements FeedbackSseClient {

	private final WebClient webClient = WebClient.builder()
		.baseUrl("http://ai-server-host")
		.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
		.build();

	@Override
	public SseEmitter streamStartFeedback(FeedbackStartRequestDto requestDto) {
		SseEmitter emitter = new SseEmitter(0L);

		webClient.post()
			.uri("/ai/feedback/start")
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
			.doOnComplete(emitter::complete)
			.subscribe();

		return emitter;
	}
}
