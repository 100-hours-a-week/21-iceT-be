package icet.koco.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackStartRequestDto {
	private Long sessionId;
	private Long problemNumber;
	private String title;
	private String inputDescription;
	private String outputDescription;
	private String inputExample;
	private String outputExample;
	private String codeLanguage;		// cpp, java, python
	private String code;
}
