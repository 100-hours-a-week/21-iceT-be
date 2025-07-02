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
		.baseUrl("http://ai-server")	// TODO: AI서버로 바꾸기
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

			return (response != null && response.length > 0) ? response[0].getSummary() : "";
		} catch (Exception e) {
			log.error("요약 요청 중 오류 발생", e);
			return "";
		}
	}
}
