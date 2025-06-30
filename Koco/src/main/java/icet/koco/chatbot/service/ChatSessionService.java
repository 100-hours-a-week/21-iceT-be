package icet.koco.chatbot.service;

import icet.koco.chatbot.client.FeedbackSseClient;
import icet.koco.chatbot.dto.ChatSessionStartRequestDto;
import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import icet.koco.chatbot.emitter.ChatEmitterRepository;
import icet.koco.chatbot.repository.ChatSessionRepository;
import icet.koco.enums.ErrorMessage;
import icet.koco.global.exception.ResourceNotFoundException;
import icet.koco.problemSet.entity.Problem;
import icet.koco.problemSet.repository.ProblemRepository;
import icet.koco.user.entity.User;
import icet.koco.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSessionService {

	private final ChatSessionRepository chatSessionRepository;
	private final ChatEmitterRepository chatEmitterRepository;
	private final FeedbackSseClient feedbackSseClient;
	private final ProblemRepository problemRepository;
	private final UserRepository userRepository;

	@Transactional
	public SseEmitter startFeedbackSession(ChatSessionStartRequestDto dto, Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

		Problem problem = problemRepository.findByNumber(dto.getProblemNumber())
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PROBLEM_NOT_FOUND));

		var session = chatSessionRepository.save(
			icet.koco.chatbot.entity.ChatSession.builder()
				.user(user)
				.mode(dto.getMode())
				.problemNumber(problem.getNumber())
				.title(problem.getTitle())
				.createdAt(LocalDateTime.now())
				.build()
		);

		SseEmitter emitter = new SseEmitter(0L);
		chatEmitterRepository.save(session.getId(), emitter);

		emitter.onCompletion(() -> chatEmitterRepository.delete(session.getId()));
		emitter.onTimeout(() -> chatEmitterRepository.delete(session.getId()));
		emitter.onError(e -> chatEmitterRepository.delete(session.getId()));

		FeedbackStartRequestDto request = FeedbackStartRequestDto.builder()
			.sessionId(session.getId())
			.problemNumber(problem.getNumber())
			.title(problem.getTitle())
			.description(problem.getDescription())
			.inputDescription(problem.getInputDescription())
			.outputDescription(problem.getOutputDescription())
			.inputExample(problem.getInputExample())
			.outputExample(problem.getOutputExample())
			.codeLanguage(dto.getLanguage())
			.code(dto.getUserCode())
			.build();

		// AI 서버에 요청하고 응답을 현재 emitter로 전달
		return feedbackSseClient.streamStartFeedback(request);
	}

	public void handleUserMessage(Long sessionId, String content) {
		// TODO: DB에 message 저장 (선택)

		// TODO: AI 서버에 후속 질문 요청하고 응답 받기
		String response = "[MOCK] '" + content + "'에 대한 응답입니다.";

		SseEmitter emitter = chatEmitterRepository.get(sessionId);
		if (emitter != null) {
			try {
				emitter.send(SseEmitter.event().name("message").data(response));
			} catch (IOException e) {
				log.error("SSE 전송 실패", e);
				emitter.completeWithError(e);
				chatEmitterRepository.delete(sessionId);
			}
		}
	}
}
