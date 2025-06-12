package icet.koco.auth;

import icet.koco.auth.dto.AuthResponse;
import icet.koco.auth.dto.LogoutResponse;
import icet.koco.auth.entity.OAuth;
import icet.koco.auth.service.AuthService;
import icet.koco.auth.service.KakaoOAuthClient;
import icet.koco.auth.dto.KakaoUserResponse;
import icet.koco.auth.repository.OAuthRepository;
import icet.koco.user.entity.User;
import icet.koco.user.repository.UserRepository;
import icet.koco.util.CookieUtil;
import icet.koco.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private KakaoOAuthClient kakaoOAuthClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuthRepository oauthRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ValueOperations<String, String> valueOperations;


    @Test
    void 가입된_유저_카카오_로그인() {
        // Given
        String code = "testCode";
        var kakaoUser = KakaoUserResponse.builder()
                .id(1234L)
                .kakao_account(KakaoUserResponse.KakaoAccount.builder()
                        .email("existUser@example.com")
                        .profile(KakaoUserResponse.KakaoAccount.Profile.builder()
                                .nickname("existUser")
                                .profile_image_url("http://example.com/existUser.jpg")
                                .build())
                        .build())
                .build();

        var user = User.builder()
                .id(1L)
                .email("existUser@example.com")
                .name("existUser")
                .build();

        given(kakaoOAuthClient.getUserInfo(code)).willReturn(kakaoUser);
        given(userRepository.findByEmail("existUser@example.com")).willReturn(Optional.of(user));
        given(jwtTokenProvider.createAccessToken(user)).willReturn("access_token");
        given(jwtTokenProvider.createRefreshToken(user)).willReturn("refresh_token");
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        var mockResponse = new MockHttpServletResponse();

        // When
        AuthResponse authResponse = authService.loginWithKakao(code, mockResponse);

        // Then
        assertThat(authResponse.getCode()).isEqualTo("LOGIN_SUCCESS");
        assertThat(authResponse.getData().getEmail()).isEqualTo("existUser@example.com");

        verify(valueOperations).set(eq("1"), eq("refresh_token"));
        verify(oauthRepository).updateRefreshToken(eq(1L), eq("refresh_token"));
    }

    @Test
    void 신규_유저_카카오_로그인() {
        // given
        String code = "testCode";
        var kakaoUser = KakaoUserResponse.builder()
                .id(5678L)
                .kakao_account(KakaoUserResponse.KakaoAccount.builder()
                        .email("newUser@example.com")
                        .profile(KakaoUserResponse.KakaoAccount.Profile.builder()
                                .nickname("newUser")
                                .profile_image_url("http://example.com/newUser.jpg")
                                .build())
                        .build())
                .build();

        var newUser = User.builder()
                .id(2L)
                .email("newUser@example.com")
                .name("newUser")
                .build();

        given(kakaoOAuthClient.getUserInfo(code)).willReturn(kakaoUser);
        given(userRepository.findByEmail("newUser@example.com")).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(newUser);
        given(jwtTokenProvider.createAccessToken(newUser)).willReturn("access_token");
        given(jwtTokenProvider.createRefreshToken(newUser)).willReturn("refresh_token");
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        var mockResponse = new MockHttpServletResponse();

        // when
        AuthResponse authResponse = authService.loginWithKakao(code, mockResponse);

        // then
        assertThat(authResponse.getCode()).isEqualTo("LOGIN_SUCCESS");
        assertThat(authResponse.getData().getEmail()).isEqualTo("newUser@example.com");

        verify(valueOperations).set(eq("2"), eq("refresh_token"));
        verify(oauthRepository).save(any(OAuth.class));
    }

    @Test
    void 정상_로그아웃() {
        // Given
        String accessToken = "validAccessToken";
        Long userId = 1L;
        long expiration = 1000L * 60 * 10; // 예: 10분

        // 쿠키 mock
        Cookie accessTokenCookie = new Cookie("access_token", accessToken);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        when(mockRequest.getCookies()).thenReturn(new Cookie[]{accessTokenCookie});

        given(jwtTokenProvider.isInvalidToken(accessToken)).willReturn(true);
        given(jwtTokenProvider.getUserIdFromToken(accessToken)).willReturn(userId);
        given(jwtTokenProvider.getExpiration(accessToken)).willReturn(expiration);

        // OAuth mock
        OAuth oAuth = OAuth.builder()
                .user(User.builder().id(userId).build())
                .refreshToken("refreshToken")
                .build();
        given(oauthRepository.findByUserId(userId)).willReturn(Optional.of(oAuth));

        // Redis mock
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        // When
        LogoutResponse logoutResponse = authService.logout(mockRequest, mockResponse);

        // Then
        assertThat(logoutResponse.getCode()).isEqualTo("LOGOUT_SUCCESS");
        assertThat(logoutResponse.getMessage()).isEqualTo("로그아웃 성공.");

        verify(oauthRepository).save(any(OAuth.class)); // refreshToken null 저장
        verify(redisTemplate).delete(userId.toString());
        verify(redisTemplate.opsForValue()).set(eq("BL:" + accessToken), eq("logout"), eq(expiration), eq(TimeUnit.MILLISECONDS));
        verify(cookieUtil).invalidateCookie(mockResponse, "access_token");
        verify(cookieUtil).invalidateCookie(mockResponse, "refresh_token");
    }

}

