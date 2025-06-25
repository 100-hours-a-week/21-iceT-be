package icet.koco.chatbot.client;

import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public interface FeedbackSseClient {
	SseEmitter streamStartFeedback(FeedbackStartRequestDto requestDto);
}
