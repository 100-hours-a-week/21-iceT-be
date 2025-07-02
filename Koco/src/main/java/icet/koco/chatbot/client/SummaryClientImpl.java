package icet.koco.chatbot.client;

import icet.koco.chatbot.dto.summary.ChatSummaryRequestDto;
import icet.koco.chatbot.dto.summary.ChatSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryClientImpl implements SummaryClient {

	private final WebClient webClient = WebClient.builder()
		.baseUrl("${AI_BASE_URL}")	// TODO: 실제 AI 서버 주소로 바꿔야 함
		.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
		.build();

	@Override
	public String requestSummary(ChatSummaryRequestDto requestDto) {
		try {
			List<ChatSummaryRequestDto> list = List.of(requestDto);
			ChatSummaryResponseDto[] response = webClient.post()
				.uri("/api/ai/v2/summary")
				.bodyValue(list)
				.retrieve()
				.bodyToMono(ChatSummaryResponseDto[].class)
				.block();

			if (response != null && response.length > 0) {
				log.info("요약 응답 수신 완료 - sessionId: {}", response[0].getSessionId());
				return response[0].getSummary();
			} else {
				log.warn("요약 응답이 비어 있음");
				return "";
			}
		} catch (Exception e) {
			log.error("요약 요청 중 오류 발생", e);
			return "";
		}
	}
}
