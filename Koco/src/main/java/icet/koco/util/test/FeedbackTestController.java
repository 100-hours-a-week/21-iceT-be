package icet.koco.util.test;

import icet.koco.chatbot.dto.feedback.FeedbackAnswerRequestDto;
import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import icet.koco.chatbot.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("api/test/feedback")
@RequiredArgsConstructor
public class FeedbackTestController {

	private final FeedbackService feedbackService;

	@PostMapping("/start")
	public SseEmitter testStart(@RequestBody FeedbackStartRequestDto dto) {
		Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return feedbackService.handleStartFeedback(dto, userId);
	}

	@PostMapping("/answer")
	public SseEmitter testAnswer(@RequestBody FeedbackAnswerRequestDto dto) {
		Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return feedbackService.handleFeedbackAnswer(dto, userId);
	}
}
