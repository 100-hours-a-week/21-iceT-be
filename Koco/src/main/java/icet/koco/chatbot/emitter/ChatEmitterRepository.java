package icet.koco.chatbot.emitter;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface ChatEmitterRepository {
	SseEmitter save(Long sessionId, SseEmitter emitter);
	SseEmitter get(Long sessionId);
	void delete(Long sessionId);
}
