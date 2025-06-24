package icet.koco.chatbot.repository;

import icet.koco.chatbot.entity.ChatSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSummaryRepository extends JpaRepository<ChatSummary, Long> {
}
