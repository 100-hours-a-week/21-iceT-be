package icet.koco.chatbot.controller;

import icet.koco.chatbot.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/backend/v2/chat")
@RequiredArgsConstructor
public class ChatSseController {

	private final ChatSessionService chatSessionService;

	// SSE 연결 유지용 엔드포인트
	@GetMapping(value = "/session/{sessionId}/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter connect(@PathVariable Long sessionId) {
		return chatSessionService.createSessionEmitter(sessionId);
	}

	// 후속 질문 보내기
	@PostMapping("/session/{sessionId}")
	public ResponseEntity<Void> userMessage(@PathVariable Long sessionId, @RequestBody ChatMessageRequestDto dto) {
		chatSessionService.handleUserMessage(sessionId, dto.getContent());
		return ResponseEntity.ok().build();
	}
}
