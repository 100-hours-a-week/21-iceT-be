package icet.koco.chatbot.controller;

import icet.koco.chatbot.dto.ChatSessionStartRequestDto;
import icet.koco.chatbot.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/backend/v2/chat")
@RequiredArgsConstructor
public class ChatSessionController {

	private final ChatSessionService chatSessionService;

	@PostMapping(value = "/session", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter startChatSession(@RequestBody ChatSessionStartRequestDto requestDto) {
		System.out.println("ChatSessionController.startChatSession");
		Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String mode = (requestDto.getMode()).toString();

		if (mode.equals("feedback")) {
			return chatSessionService.startFeedbackSession(requestDto, userId);
		}
		return null;
	}
}
