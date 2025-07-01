package icet.koco.chatbot.client;

import icet.koco.chatbot.dto.feedback.FeedbackAnswerRequestDto;
import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import icet.koco.chatbot.emitter.ChatEmitterRepository;
import icet.koco.chatbot.entity.ChatRecord.Role;
import icet.koco.chatbot.entity.ChatSession;
import icet.koco.chatbot.repository.ChatRecordRepository;
import icet.koco.chatbot.repository.ChatSessionRepository;
import icet.koco.chatbot.service.ChatRecordService;
import icet.koco.enums.ErrorMessage;
import icet.koco.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Primary
@RequiredArgsConstructor
public class MockFeedbackSseClient implements FeedbackSseClient {

	private final ChatEmitterRepository chatEmitterRepository;
	private final ChatSessionRepository chatSessionRepository;
	private final ChatRecordRepository chatRecordRepository;
	private final ChatRecordService chatRecordService;

	@Override
	public SseEmitter streamStartFeedback(FeedbackStartRequestDto requestDto) {
		System.out.println("MOCK 세션 생성 - sessionId: " + requestDto.getSessionId());

		SseEmitter emitter = new SseEmitter(60_000L);
		chatEmitterRepository.save(requestDto.getSessionId(), emitter);

		new Thread(() -> {
			// 응답을 누적해서 chatRecord에 저장하기 위해
			StringBuilder fullResponse = new StringBuilder();

			try {
				String msg1 = "\n🧪 MOCK: 세션 시작 - 문제 제목: \n" + requestDto.getTitle();
				emitter.send(SseEmitter.event().name("message").data(msg1));
				fullResponse.append(msg1);
				Thread.sleep(500);

				String msg2 = "\n💬 MOCK: 사용자 코드 분석 중...\n" + requestDto.getCode();
				emitter.send(SseEmitter.event().name("message").data(msg2));
				fullResponse.append(msg2);
				Thread.sleep(500);

				String msg3 = "\n✅ MOCK: 코드 분석 완료!";
				emitter.send(SseEmitter.event().name("message").data(msg3));
				fullResponse.append(msg3);

				emitter.complete();

				// chatSession 조회
				ChatSession chatSession = chatSessionRepository.findById(requestDto.getSessionId())
					.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CHAT_SESSION_NOT_FOUND));

				// turn 자동 증가
				int nextTurn = chatRecordRepository.findMaxTurnBySessionId(requestDto.getSessionId());

				// chatRecord 저장
				chatRecordService.save(chatSession, Role.assistant, fullResponse.toString());

			} catch (Exception e) {
				System.out.println("SseEmitter 오류 발생: " + e.getMessage());
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
			throw new IllegalStateException("emitter not found for sessionId: " + requestDto.getSessionId());
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
