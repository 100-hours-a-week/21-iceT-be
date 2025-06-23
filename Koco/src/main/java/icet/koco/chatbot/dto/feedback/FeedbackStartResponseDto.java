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
public class FeedbackStartResponseDto {
	private String sessionId;
	private int problemNumber;
	private String title;
	private List<String> good;
	private List<String> bad;
	private String improvedCode;
}
