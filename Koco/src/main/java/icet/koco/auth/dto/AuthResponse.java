package icet.koco.auth.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String code;
    private String message;
    private AuthData data;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthData {
        private String email;
        private String name;
        private boolean isRegistered;
    }
}