package icet.koco.chatbot.client;

import icet.koco.chatbot.dto.feedback.FeedbackAnswerRequestDto;
import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import icet.koco.chatbot.dto.summary.ChatSummaryRequestDto;
import icet.koco.chatbot.entity.ChatSummary;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiServerClient {
	void streamStartFeedback(FeedbackStartRequestDto dto, SseEmitter emitter);
	void streamFeedbackAnswer(FeedbackAnswerRequestDto dto, SseEmitter emitter);
	ChatSummary requestSummary(ChatSummaryRequestDto dto);
}
