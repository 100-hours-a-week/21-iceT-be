package icet.koco.chatbot.dto.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import icet.koco.chatbot.entity.ChatRecord;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSummaryRequestDto {
	private Long sessionId;
	private String mode;
	private List<Message> messages;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Message {
		private String role;      // "user" 아님 "assistant"
		private String content;
	}

	public static ChatSummaryRequestDto from(List<ChatRecord> records) {
		List<ChatSummaryRequestDto.Message> messages = records.stream()
			.map(record -> new ChatSummaryRequestDto.Message(record.getRole().name(), record.getContent()))
			.collect(Collectors.toList());

		return ChatSummaryRequestDto.builder()
			.sessionId(records.get(0).getChatSession().getId())
			.mode("interview")
			.messages(messages)
			.build();
	}
}
