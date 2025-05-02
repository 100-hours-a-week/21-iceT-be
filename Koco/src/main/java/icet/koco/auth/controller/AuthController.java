package icet.koco.auth.controller;

import icet.koco.auth.dto.*;
import icet.koco.auth.service.AuthService;
import icet.koco.auth.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final LogoutService logoutService;

    @CrossOrigin(origins = "*")
    @GetMapping("/callback")
    public ResponseEntity<AuthResponse> kakaoCallback(@RequestParam("code") String code,
        HttpServletResponse response) {
        System.out.println("인가코드: " + code);
        AuthResponse authResponse = authService.loginWithKakao(code, response);
        return ResponseEntity.ok(authResponse);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(logoutService.logout(request, response));
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(
        @CookieValue(value = "refresh_token", required = true) String refreshToken,
        HttpServletResponse response)
    {
        System.out.println("refreshToken: " + refreshToken);
        RefreshResponse refreshResponse = authService.refreshAccessToken(refreshToken, response);
        return ResponseEntity.ok(refreshResponse);
    }
}