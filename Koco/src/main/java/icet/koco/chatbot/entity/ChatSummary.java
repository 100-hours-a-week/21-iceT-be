package icet.koco.chatbot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
@Table(name = "chat_summary")
public class ChatSummary {
	@Id
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id")
	private ChatSession chatSession;

	@Column(name = "turn", nullable = false)
	private Integer turn;

	@Column(name = "summary", columnDefinition = "TEXT")
	private String summary;

	@Column(name = "created_at")
	private LocalDateTime createdAt;
}
