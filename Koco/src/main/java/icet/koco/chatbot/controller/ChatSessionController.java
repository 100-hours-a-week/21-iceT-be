package icet.koco.chatbot.controller;

import icet.koco.chatbot.dto.ChatSessionStartRequestDto;
import icet.koco.chatbot.dto.UserMessageRequestDto;
import icet.koco.chatbot.entity.ChatSession;
import icet.koco.chatbot.repository.ChatSessionRepository;
import icet.koco.chatbot.service.ChatSessionService;
import icet.koco.enums.ErrorMessage;
import icet.koco.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/backend/v2/chat")
@RequiredArgsConstructor
public class ChatSessionController {

	private final ChatSessionService chatSessionService;
	private final ChatSessionRepository chatSessionRepository;

	@PostMapping(value = "/session", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter startChatSession(@RequestBody ChatSessionStartRequestDto requestDto) {
		Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String mode = (requestDto.getMode()).toString();

		if (mode.equals("feedback")) {
			return chatSessionService.startFeedbackSession(requestDto, userId);
		} else if (mode.equals("interview")) {
			return chatSessionService.startInterviewSession(requestDto, userId);
		}
		return null;
	}

	@PostMapping(value = "/session/{sessionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter sendMessage(@PathVariable Long sessionId,
											@RequestBody UserMessageRequestDto requestDto) {
		Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String mode = getMode(sessionId).toString();

		if (mode.equals("feedback")) {
			return chatSessionService.followupFeedbackSession(sessionId, userId, requestDto.getContent());
		} else if (mode.equals("interview")) {
			return chatSessionService.followupInterviewSession(sessionId, userId, requestDto.getContent());
		}
		return null;
	}

	/**
	 * 세션의 모드를 구함
	 * @param sessionId 세션id
	 * @return ChatSession.Mode
	 */
	public ChatSession.Mode getMode(Long sessionId) {
		ChatSession chatSession = chatSessionRepository.findById(sessionId)
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CHAT_SESSION_NOT_FOUND));

		return chatSession.getMode();
	}
}
