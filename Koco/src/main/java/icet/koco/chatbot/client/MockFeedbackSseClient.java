package icet.koco.chatbot.client;

import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Primary
public class MockFeedbackSseClient implements FeedbackSseClient {

	@Override
	public SseEmitter streamStartFeedback(FeedbackStartRequestDto dto) {
		System.out.println("MockFeebackSseClient: streamStartFeedback");
		SseEmitter emitter = new SseEmitter();

		new Thread(() -> {
			try {
				emitter.send(SseEmitter.event().name("message").data("🧪 MOCK: 세션 시작"));
				Thread.sleep(500);
				emitter.send(SseEmitter.event().name("message").data("💬 MOCK: 코드 분석 중..."));
				Thread.sleep(500);
				emitter.send(SseEmitter.event().name("message").data("✅ MOCK: 분석 완료"));
				emitter.complete();
			} catch (Exception e) {
				emitter.completeWithError(e);
			}
		}).start();

		return emitter;
	}
}
