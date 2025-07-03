package icet.koco.chatbot.service;

import icet.koco.chatbot.client.FeedbackSseClient;
import icet.koco.chatbot.client.InterviewSseClient;
import icet.koco.chatbot.client.SummaryClient;
import icet.koco.chatbot.dto.ChatSessionStartRequestDto;
import icet.koco.chatbot.dto.ai.ChatbotFollowupRequestDto;
import icet.koco.chatbot.dto.ai.ChatbotStartRequestDto;
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

	private final ChatRecordService chatRecordService;

	private final ChatSessionRepository chatSessionRepository;
	private final ProblemRepository problemRepository;
	private final UserRepository userRepository;
	private final ChatRecordRepository chatRecordRepository;
	private final ChatSummaryRepository chatSummaryRepository;

	private final FeedbackSseClient feedbackSseClient;
	private final InterviewSseClient interviewSseClient;
	private final SummaryClient summaryClient;

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
		ChatbotStartRequestDto request = ChatbotStartRequestDto.builder()
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

	public SseEmitter followupFeedbackSession (Long sessionId, Long userId, String content) {
		// 사용자 있는지 확인
		userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

		// 채팅 세션 찾기
		ChatSession session = chatSessionRepository.findById(sessionId)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CHAT_SESSION_NOT_FOUND));

		// 유저가 보낸 메시지 저장
		chatRecordService.save(session, ChatRecord.Role.user, content);

		// 전체 메시지 수 체크 (해당 세션 기준)
		long totalCount = chatRecordRepository.countByChatSession(session);

		String summary;
		boolean shouldUpdateSummary = (totalCount == 3 || totalCount % 11 == 0);

		if (shouldUpdateSummary) {
			System.out.println("summary 요청 | totalCount: " + totalCount);
			List<ChatRecord> latestMessages = chatRecordRepository.findLast10BySessionId(sessionId);
			ChatSummaryRequestDto summaryDto = ChatSummaryRequestDto.from(sessionId, latestMessages);

			summary = summaryClient.requestSummary(summaryDto);
			saveOrUpdateSummary(session, summary);
		} else {
			// 기존 summary 가져오기
			System.out.println("summary 요청하지 않음 | totalCount: " + totalCount);
			summary = chatSummaryRepository.findByChatSession(session)
					.map(ChatSummary::getSummary)
					.orElse("");
		}

		// 후속 피드백 요청
		List<ChatRecord> latestMessages = chatRecordRepository.findLast10BySessionId(sessionId);
		ChatbotFollowupRequestDto answerRequest = ChatbotFollowupRequestDto.from(sessionId, summary, latestMessages);
		return feedbackSseClient.streamFollowupFeedback(answerRequest);
	}

	public SseEmitter startInterviewSession(ChatSessionStartRequestDto dto, Long userId) {
		log.info("startInterviewSession - userId: " + userId);
		// 사용자 찾기
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

		log.info("User: " + user.getNickname());
		log.info("Mode: " + dto.getMode());

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

		// 사용자 메세지 저장
		List<ChatRecord> initRecord = ChatRecord.fromStartDto(dto, session);
		chatRecordRepository.saveAll(initRecord);

		// AI 피드백 요청
		ChatbotStartRequestDto request = ChatbotStartRequestDto.builder()
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

		return interviewSseClient.startInterviewSession(request);
	}

	public SseEmitter followupInterviewSession(Long sessionId, Long userId, String content) {
		log.info("FollowupInterviewSession - sessionId: " + sessionId + ", userId: " + userId);
		// 사용자 있는지 확인
		userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

		// 채팅 세션 찾기
		ChatSession chatSession = chatSessionRepository.findById(sessionId)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CHAT_SESSION_NOT_FOUND));

		// 유저가 보낸 메세지 저장
		chatRecordService.save(chatSession, ChatRecord.Role.user, content);

		// 전체 메세지 수 체크
		Long totalCount = chatRecordRepository.countByChatSession(chatSession);

		// 요약된 메세지
		String summary;

		boolean shouldUpdateSummary = (totalCount == 3 || totalCount % 11 == 0);

		if (shouldUpdateSummary) {
			log.info("요약 요청, totalCount: " + totalCount);
			List<ChatRecord> latestMessages = chatRecordRepository.findLast10BySessionId(sessionId);
			ChatSummaryRequestDto summaryDto = ChatSummaryRequestDto.from(sessionId, latestMessages);

			summary = summaryClient.requestSummary(summaryDto);
		} else {
			// 기존 summary 사용
			log.info("summary 요청하지 않음, totalCount: " + totalCount);
			summary = chatSummaryRepository.findByChatSession(chatSession)
					.map(ChatSummary::getSummary)
					.orElse("");
		}

		// 후속 인터뷰 AI 요청
		List<ChatRecord> latestMessages = chatRecordRepository.findLast10BySessionId(sessionId);
		ChatbotFollowupRequestDto answerRequest = ChatbotFollowupRequestDto.from(sessionId, summary, latestMessages);
		return interviewSseClient.streamFollowupInterview(answerRequest);
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
		System.out.println("summary 저장 또는 업데이트 완료");
	}

}
