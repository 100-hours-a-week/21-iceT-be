package icet.koco.auth.service;

import icet.koco.auth.dto.KakaoTokenResponse;
import icet.koco.auth.dto.KakaoUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KakaoOAuthClient {

    @Value("${KAKAO_CLIENT_ID}")
    private String clientId;

    @Value("${KAKAO_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${KAKAO_REDIRECT_URI}")
    private String redirectUri;

    private final WebClient webClient = WebClient.create();

    public KakaoUserResponse getUserInfo(String code) {
        System.out.println("받은 인가코드: " + code);
        System.out.println("clientId: " + clientId);
        System.out.println("redirectUri: " + redirectUri);
        String token = webClient.post()
            .uri("https://kauth.kakao.com/oauth/token")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .bodyValue("grant_type=authorization_code&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&redirect_uri=" + redirectUri + "&code=" + code)
            .retrieve()
            .bodyToMono(KakaoTokenResponse.class)
            .block()
            .getAccess_token();

        return webClient.get()
            .uri("https://kapi.kakao.com/v2/user/me")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .bodyToMono(KakaoUserResponse.class)
            .block();
    }
}

