package icet.koco.chatbot.dto.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryRequestDto {
	private String sessionId;
	private String mode;
	private List<Message> messages;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Message {
		private String role;      // "user" 아님 "assistant"
		private String content;    
	}
}
