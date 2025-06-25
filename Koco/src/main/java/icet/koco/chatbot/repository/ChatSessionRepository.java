package icet.koco.chatbot.repository;

import icet.koco.chatbot.entity.ChatRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatRecord, Long> {
}
