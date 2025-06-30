package icet.koco.chatbot.service;

import icet.koco.chatbot.client.FeedbackSseClient;
import icet.koco.chatbot.dto.ChatSessionStartRequestDto;
import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import icet.koco.chatbot.entity.ChatSession;
import icet.koco.chatbot.repository.ChatSessionRepository;
import icet.koco.enums.ErrorMessage;
import icet.koco.global.exception.ResourceNotFoundException;
import icet.koco.problemSet.entity.Problem;
import icet.koco.problemSet.repository.ProblemRepository;
import icet.koco.user.entity.User;
import icet.koco.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatSessionService {

	private final ChatSessionRepository chatSessionRepository;
	private final FeedbackSseClient feedbackSseClient;
	private final ProblemRepository problemRepository;
	private final UserRepository userRepository;

	public SseEmitter startFeedbackSession(ChatSessionStartRequestDto dto, Long userId) {
		if (!dto.getMode().equals("feedback")) {
			throw new IllegalArgumentException("Only 'feedback mode is supported");
		}

		// 사용자 찾기
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

		// 문제 찾기
		Problem problem = problemRepository.findByNumber(dto.getProblemNumber())
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PROBLEM_NOT_FOUND));

		ChatSession session = chatSessionRepository.save(
			ChatSession.builder()
				.user(user)
				.mode(dto.getMode())
				.problemNumber(problem.getNumber())
				.title(problem.getTitle())
				.createdAt(LocalDateTime.now())
				.build()
		);

		FeedbackStartRequestDto request = FeedbackStartRequestDto.builder()
			.sessionId(session.getId())
			.problemNumber(dto.getProblemNumber())
			.title(problem.getTitle())
			.description(problem.getDescription())
			.inputDescription(problem.getInputDescription())
			.outputDescription(problem.getOutputDescription())
			.inputExample(problem.getInputExample())
			.outputExample(problem.getOutputExample())
			.codeLanguage(dto.getLanguage())
			.code(dto.getUserCode())
			.build();

		return feedbackSseClient.streamStartFeedback(request);
	}
}
