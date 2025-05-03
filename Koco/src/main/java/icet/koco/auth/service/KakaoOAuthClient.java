package icet.koco.auth.service;

import icet.koco.auth.dto.KakaoTokenResponse;
import icet.koco.auth.dto.KakaoUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {

    private final WebClient kakaoWebClient; // WebClient 주입

    @Value("${KAKAO_CLIENT_ID}")
    private String clientId;

    @Value("${KAKAO_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${KAKAO_REDIRECT_URI}")
    private String redirectUri;

    public KakaoUserResponse getUserInfo(String code) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);



        String token = kakaoWebClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block()
                .getAccess_token();
        return kakaoWebClient.get()
            .uri("https://kapi.kakao.com/v2/user/me")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .bodyToMono(KakaoUserResponse.class)
            .block();
    }

    public void unlinkUser(String kakaoUserId) {
        try {
            String response = kakaoWebClient.post()
                .uri("/v1/user/unlink")
                .bodyValue("target_id_type=user_id&target_id=" + kakaoUserId)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info(">>>>> Kakao 사용자 연결 끊기 성공: {}", response);
        } catch (Exception e) {
            log.warn(">>>>> Kakao unlink 실패: {}", e.getMessage());

        }
    }
}
