package icet.koco.auth.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoUserResponse {
    private Long id;
    private KakaoAccount kakao_account;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KakaoAccount {
        private String email;
        private String name;
    }

    public String getEmail() {
        return kakao_account.getEmail();
    }

    public String getName() {
        return kakao_account.getName();
    }

    public String getProviderId() {
        return String.valueOf(id);
    }
}