package icet.koco.chatbot.dto.feedback;

import icet.koco.chatbot.entity.ChatRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAnswerRequestDto {
	private Long sessionId;
	private String summary;
	private List<Message> messages;

	@Data
	@AllArgsConstructor
	public static class Message {
		private String role;
		private String content;
	}

	public static FeedbackAnswerRequestDto from(Long sessionId, String summary, List<ChatRecord> messages) {
		List<Message> messageList = messages.stream()
				.map(r -> new Message(r.getRole().name(), r.getContent()))
				.collect(Collectors.toList());

		return FeedbackAnswerRequestDto.builder()
				.sessionId(sessionId)
				.summary(summary)
				.messages(messageList)
				.build();
	}
}
