package icet.koco.chatbot.service;

import icet.koco.chatbot.client.AiServerClient;
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
import icet.koco.user.entity.User;
import icet.koco.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

	private final AiServerClient aiClient;
	private final ChatRecordRepository chatRecordRepository;
	private final ChatSummaryRepository chatSummaryRepository;
	private final ChatSessionRepository chatSessionRepository;
	private final UserRepository userRepository;

	public SseEmitter handleStartFeedback(FeedbackStartRequestDto dto, Long userId) {
		User user =  userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

		ChatSession chatSession = chatSessionRepository.save(
			ChatSession.builder()
				.user(user)
				.build()
		);

		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

		aiClient.streamStartFeedback(dto, emitter);

		return emitter;
	}

//	public SseEmitter handleFeedbackAnswer(FeedbackAnswerRequestDto dto, Long userId) {
//		ChatSession chatSession = chatSessionRepository.findById(dto.getSessionId())
//			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CHAT_SESSION_NOT_FOUND));
//
//		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
//
//		aiClient.streamFeedbackAnswer(dto, emitter);
//
//		// 메시지 저장
//		chatRecordRepository.saveAll(ChatRecord.fromAnswerDto(dto, chatSession));
//
//		// 메시지 수가 10개 되면 요약 요청
//		List<ChatRecord> records = chatRecordRepository.findLast10BySessionId(dto.getSessionId());
//		if (records.size() == 10) {
//			ChatSummaryRequestDto summaryDto = ChatSummaryRequestDto.from(records);
//			ChatSummary summary = requestSummary(summaryDto);
//			chatSummaryRepository.save(summary);
//		}
//
//		return emitter;
//	}
//
//	public ChatSummary requestSummary(ChatSummaryRequestDto dto) {
//		return aiClient.requestSummary(dto);
//	}
}
