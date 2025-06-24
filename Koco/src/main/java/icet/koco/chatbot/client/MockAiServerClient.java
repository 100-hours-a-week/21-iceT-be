package icet.koco.chatbot.client;

import icet.koco.chatbot.dto.feedback.FeedbackAnswerRequestDto;
import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import icet.koco.chatbot.dto.summary.ChatSummaryRequestDto;
import icet.koco.chatbot.entity.ChatSession;
import icet.koco.chatbot.entity.ChatSummary;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
@Primary
public class MockAiServerClient implements AiServerClient {

	@Async
	@Override
	public void streamStartFeedback(FeedbackStartRequestDto dto, SseEmitter emitter) {
		try {
			emitter.send(SseEmitter.event().name("message").data("👋 피드백 세션 시작합니다.\n"));
			Thread.sleep(1000);
			emitter.send(SseEmitter.event().name("message").data("💡 코드 분석 중입니다...\n"));
			Thread.sleep(1000);
			emitter.send(SseEmitter.event().name("message").data("✅ 분석 완료: 변수명 명확합니다.\n\n"));
			emitter.complete();
		} catch (Exception e) {
			emitter.completeWithError(e);
		}
	}


	@Override
	public void streamFeedbackAnswer(FeedbackAnswerRequestDto dto, SseEmitter emitter) {
		try {
			emitter.send(SseEmitter.event().data(">>> Mock Answer: 후속 피드백"));
			emitter.complete();
		} catch (IOException e) {
			emitter.completeWithError(e);
		}
	}

	@Override
	public ChatSummary requestSummary(ChatSummaryRequestDto dto) {
		ChatSession session = ChatSession.builder()
			.id(dto.getSessionId()) // sessionId만 있는 dummy 객체 생성
			.build();

		return ChatSummary.builder()
			.chatSession(session)
			.summary("Mock 요약: 테스트 요약입니다.")
			.build();
	}
}
