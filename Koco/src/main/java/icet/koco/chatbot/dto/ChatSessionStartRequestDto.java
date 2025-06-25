package icet.koco.chatbot.dto;

import icet.koco.enums.ChatbotMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatSessionStartRequestDto {
	private Long problemNumber;
	private String language;
	private ChatbotMode mode;
	private String userCode;

}
