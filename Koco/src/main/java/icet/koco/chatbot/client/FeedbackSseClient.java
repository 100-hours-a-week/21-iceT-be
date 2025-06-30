package icet.koco.chatbot.client;

import icet.koco.chatbot.dto.feedback.FeedbackAnswerRequestDto;
import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface FeedbackSseClient {
	SseEmitter streamStartFeedback(FeedbackStartRequestDto requestDto);
	SseEmitter streamAnswer(FeedbackAnswerRequestDto requestDto);
}
