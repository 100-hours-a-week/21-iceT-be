package icet.koco.chatbot.service;

import icet.koco.chatbot.client.FeedbackSseClient;
import icet.koco.chatbot.dto.ChatSessionStartRequestDto;
import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import icet.koco.chatbot.entity.ChatRecord;
import icet.koco.chatbot.entity.ChatRecord.Role;
import icet.koco.chatbot.entity.ChatSession;
import icet.koco.chatbot.repository.ChatRecordRepository;
import icet.koco.chatbot.repository.ChatSessionRepository;
import icet.koco.enums.ErrorMessage;
import icet.koco.global.exception.ResourceNotFoundException;
import icet.koco.problemSet.entity.Problem;
import icet.koco.problemSet.repository.ProblemRepository;
import icet.koco.user.entity.User;
import icet.koco.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSessionService {

	private final ChatSessionRepository chatSessionRepository;
	private final FeedbackSseClient feedbackSseClient;
	private final ProblemRepository problemRepository;
	private final UserRepository userRepository;
	private final ChatRecordRepository chatRecordRepository;

	public SseEmitter startFeedbackSession(ChatSessionStartRequestDto dto, Long userId) {
		// 사용자 찾기
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

		log.info("User: " + user.getNickname());
		log.info("Mode: " + dto.getMode());

		// 문제 찾기
		Problem problem = problemRepository.findByNumber(dto.getProblemNumber())
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PROBLEM_NOT_FOUND));

		// 채팅 세션 생성
		ChatSession session = chatSessionRepository.save(
			ChatSession.builder()
				.user(user)
				.mode(dto.getMode())
				.problemNumber(problem.getNumber())
				.title(problem.getTitle())
				.createdAt(LocalDateTime.now())
				.build()
		);

		// 사용자 메세지 저장
		List<ChatRecord> initRecord = ChatRecord.fromStartDto(dto, session);
		chatRecordRepository.saveAll(initRecord);

		// AI 피드백 요청
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
