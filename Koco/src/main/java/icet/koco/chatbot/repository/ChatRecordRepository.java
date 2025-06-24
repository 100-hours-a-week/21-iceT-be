package icet.koco.chatbot.repository;

import icet.koco.chatbot.entity.ChatRecord;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRecordRepository extends JpaRepository<ChatRecord, Long> {

	@Query("SELECT c FROM ChatRecord c WHERE c.chatSession.id = :sessionId ORDER BY c.createdAt DESC")
	List<ChatRecord> findLast10BySessionId(@Param("sessionId") Long sessionId, Pageable pageable);

	default List<ChatRecord> findLast10BySessionId(Long sessionId) {
		return findLast10BySessionId(sessionId, PageRequest.of(0, 10));
	}
}
