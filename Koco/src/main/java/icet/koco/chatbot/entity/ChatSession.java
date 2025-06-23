package icet.koco.chatbot.entity;


import icet.koco.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
@Table(name = "chat_session")
public class ChatSession {
	@Id
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "mode")
	private Mode mode;

	@Column(name = "problem_number")
	private Long problemNumber;

	@Column(name = "title")
	private String title;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public enum Mode {
		feedback, interview
	}
}
