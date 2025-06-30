package icet.koco.chatbot.client;

import icet.koco.chatbot.dto.feedback.FeedbackAnswerRequestDto;
import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import icet.koco.chatbot.dto.summary.ChatSummaryRequestDto;
import icet.koco.chatbot.entity.ChatSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AiServerClientImpl implements AiServerClient {

	private final WebClient webClient = WebClient.builder().baseUrl("https://ktbkoco.com").build();

	@Override
	public void streamStartFeedback(FeedbackStartRequestDto dto, SseEmitter emitter) {
		webClient.post()
			.uri("/api/ai/v2/feedback/start")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(dto)
			.retrieve()
			.bodyToFlux(String.class)
			.doOnNext(data -> sendToSse(emitter, data))
			.doOnError(error -> emitter.completeWithError(error))
			.doOnComplete(emitter::complete)
			.subscribe();
	}

	@Override
	public void streamFeedbackAnswer(FeedbackAnswerRequestDto dto, SseEmitter emitter) {
		webClient.post()
			.uri("/api/v1/feedback/answer")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(dto)
			.retrieve()
			.bodyToFlux(String.class)
			.doOnNext(data -> sendToSse(emitter, data))
			.doOnError(error -> emitter.completeWithError(error))
			.doOnComplete(emitter::complete)
			.subscribe();
	}

	@Override
	public ChatSummary requestSummary(ChatSummaryRequestDto dto) {
		return webClient.post()
			.uri("/api/ai/v2/summary")
			.bodyValue(dto)
			.retrieve()
			.bodyToMono(ChatSummary.class)
			.block();
	}

	private void sendToSse(SseEmitter emitter, String data) {
		try {
			emitter.send(SseEmitter.event().data(data));
		} catch (IOException e) {
			emitter.completeWithError(e);
		}
	}
}
