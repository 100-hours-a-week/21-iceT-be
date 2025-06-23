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
@Table(name = "chat_record")
public class ChatRecord {
	@Id
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id")
	private ChatSession chatSession;

	@Column(name = "turn")
	private Integer turn;

	@Column(name = "role")
	private Role role;

	@Lob
	@Column(name = "content", columnDefinition = "LONGTEXT")
	private String content;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public enum Role {
		user, assistant
	}
}
