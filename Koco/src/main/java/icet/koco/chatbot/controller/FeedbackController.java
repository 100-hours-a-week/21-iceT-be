package icet.koco.chatbot.controller;

import icet.koco.chatbot.dto.feedback.FeedbackAnswerRequestDto;
import icet.koco.chatbot.dto.feedback.FeedbackStartRequestDto;
import icet.koco.chatbot.dto.summary.ChatSummaryRequestDto;
import icet.koco.chatbot.entity.ChatSummary;
import icet.koco.chatbot.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FeedbackController {

	private final FeedbackService feedbackService;

	@PostMapping(value = "/ai/v2/feedback/start", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter startFeedback(@RequestBody FeedbackStartRequestDto requestDto) {
		Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return feedbackService.handleStartFeedback(requestDto, userId);
	}

	@PostMapping(value = "/v1/feedback/answer", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter feedbackAnswer(@RequestBody FeedbackAnswerRequestDto requestDto) {
		Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return feedbackService.handleFeedbackAnswer(requestDto, userId);
	}

	@PostMapping("/ai/v2/summary")
	public ResponseEntity<ChatSummary> summarize(@RequestBody ChatSummaryRequestDto dto) {
		ChatSummary summary = feedbackService.requestSummary(dto);
		return ResponseEntity.ok(summary);
	}
}
