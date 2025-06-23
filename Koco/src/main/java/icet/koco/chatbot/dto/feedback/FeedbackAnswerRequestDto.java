package icet.koco.chatbot.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAnswerRequestDto {
	private Long sessionId;
	private String summary;
	private List<Message> messages;

	@Data
	public static class Message {
		private String role;
		private String content;
	}
}
