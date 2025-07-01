package icet.koco.chatbot.client;

import icet.koco.chatbot.dto.feedback.FeedbackAnswerRequestDto;
import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import icet.koco.chatbot.emitter.ChatEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Primary
@RequiredArgsConstructor
public class MockFeedbackSseClient implements FeedbackSseClient {

	private final ChatEmitterRepository chatEmitterRepository;

	@Override
	public SseEmitter streamStartFeedback(FeedbackStartRequestDto requestDto) {
		System.out.println("✅ MOCK 세션 생성 - sessionId: " + requestDto.getSessionId());
		System.out.println("📌 코드 내용:\n" + requestDto.getCode());

		SseEmitter emitter = new SseEmitter(60_000L); // 60초 타임아웃
		chatEmitterRepository.save(requestDto.getSessionId(), emitter); // 💡 저장

		new Thread(() -> {
			try {
				emitter.send(SseEmitter.event().name("message").data("🧪 MOCK: 세션 시작 - 문제 제목: " + requestDto.getTitle()));
				Thread.sleep(500);
				emitter.send(SseEmitter.event().name("message").data("💬 MOCK: 사용자 코드 분석 중...\n\n" + requestDto.getCode()));
				Thread.sleep(500);
				emitter.send(SseEmitter.event().name("message").data("✅ MOCK: 코드 분석 완료!"));
				emitter.complete();
			} catch (Exception e) {
				System.out.println("❗ SseEmitter 오류 발생: " + e.getMessage());
				emitter.completeWithError(e);
			}
		}).start();

		return emitter;
	}

	@Override
	public SseEmitter streamAnswer(FeedbackAnswerRequestDto requestDto) {
		System.out.println("MockFeedbackSseClient: streamAnswer(후속 질문 AI)");
		SseEmitter emitter = chatEmitterRepository.findBySessionId(requestDto.getSessionId());

		if (emitter == null) {
			throw new IllegalStateException("❗ emitter not found for sessionId: " + requestDto.getSessionId());
		}

		new Thread(() -> {
			try {
				emitter.send(SseEmitter.event().name("message").data("💬 MOCK: 후속 질문 처리 중..."));
				Thread.sleep(500);
				emitter.send(SseEmitter.event().name("message").data("✅ MOCK: 답변 완료"));
				emitter.complete();
			} catch (Exception e) {
				emitter.completeWithError(e);
			}
		}).start();

		return emitter;
	}
}
