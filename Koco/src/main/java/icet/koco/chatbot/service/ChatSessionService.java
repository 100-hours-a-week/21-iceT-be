package icet.koco.chatbot.service;

import icet.koco.chatbot.client.FeedbackSseClient;
import icet.koco.chatbot.client.SummaryClient;
import icet.koco.chatbot.dto.ChatSessionStartRequestDto;
import icet.koco.chatbot.dto.feedback.FeedbackAnswerRequestDto;
import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import icet.koco.chatbot.dto.summary.ChatSummaryRequestDto;
import icet.koco.chatbot.entity.ChatRecord;
import icet.koco.chatbot.entity.ChatSession;
import icet.koco.chatbot.entity.ChatSummary;
import icet.koco.chatbot.repository.ChatRecordRepository;
import icet.koco.chatbot.repository.ChatSessionRepository;
import icet.koco.chatbot.repository.ChatSummaryRepository;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSessionService {

	private final ChatSessionRepository chatSessionRepository;
	private final FeedbackSseClient feedbackSseClient;
	private final SummaryClient summaryClient;
	private final ProblemRepository problemRepository;
	private final UserRepository userRepository;
	private final ChatRecordRepository chatRecordRepository;
	private final ChatRecordService chatRecordService;
	private final ChatSummaryRepository chatSummaryRepository;

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

		return feedbackSseClient.startFeedbackSession(request);
	}

	public SseEmitter processUserMessage(Long sessionId, Long userId, String content) {
		// 사용자 있는지 확인
		userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

		// 채팅 세션 찾기
		ChatSession session = chatSessionRepository.findById(sessionId)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CHAT_SESSION_NOT_FOUND));

		// 유저가 보낸 메시지 저장
		chatRecordService.save(session, ChatRecord.Role.user, content);

		List<ChatRecord> latestMessages = chatRecordRepository.findLast10BySessionId(sessionId);
		ChatSummaryRequestDto summaryDto = ChatSummaryRequestDto.from(sessionId, latestMessages);

		String summary = summaryClient.requestSummary(summaryDto);

		// 채팅 세션에 대한 요약이 없으면 새로 저장
		saveOrUpdateSummary(session, summary);

		FeedbackAnswerRequestDto answerRequest = FeedbackAnswerRequestDto.from(sessionId, summary, latestMessages);
		return feedbackSseClient.streamFollowupFeedback(answerRequest);
	}

	private void saveOrUpdateSummary(ChatSession session, String summary) {
		Optional<ChatSummary> optional = chatSummaryRepository.findByChatSession(session);
		if (optional.isPresent()) {
			ChatSummary existing = optional.get();
			existing.setSummary(summary);
			existing.setCreatedAt(LocalDateTime.now());
			chatSummaryRepository.save(existing);
		} else {
			ChatSummary newOne = ChatSummary.builder()
					.chatSession(session)
					.summary(summary)
					.createdAt(LocalDateTime.now())
					.build();
			chatSummaryRepository.save(newOne);
		}
	}

}
