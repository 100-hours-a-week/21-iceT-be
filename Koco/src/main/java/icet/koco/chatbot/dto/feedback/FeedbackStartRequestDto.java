package icet.koco.chatbot.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackStartRequestDto {
	private Long sessionId;
	private Long problemNumber;
	private String title;
	private String description;
	private String inputDescription;
	private String outputDescription;
	private String inputExample;
	private String outputExample;
	private String codeLanguage;
	private String code;
}
