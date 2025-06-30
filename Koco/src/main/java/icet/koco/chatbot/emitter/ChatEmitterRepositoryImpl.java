package icet.koco.chatbot.emitter;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ChatEmitterRepositoryImpl implements ChatEmitterRepository {

	private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

	@Override
	public SseEmitter save(Long sessionId, SseEmitter emitter) {
		emitters.put(sessionId, emitter);
		return emitter;
	}

	@Override
	public SseEmitter get(Long sessionId) {
		return emitters.get(sessionId);
	}

	@Override
	public void delete(Long sessionId) {
		emitters.remove(sessionId);
	}
}
