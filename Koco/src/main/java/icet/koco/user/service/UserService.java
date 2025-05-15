package icet.koco.user.service;

import icet.koco.auth.entity.OAuth;
import icet.koco.auth.repository.OAuthRepository;
import icet.koco.auth.service.KakaoOAuthClient;
import icet.koco.global.exception.ResourceNotFoundException;
import icet.koco.global.exception.UnauthorizedException;
import icet.koco.problemSet.repository.ProblemSetRepository;
import icet.koco.problemSet.repository.SurveyRepository;
import icet.koco.user.dto.UserAlgorithmStatsResponseDto;
import icet.koco.user.dto.UserCategoryStatDto;
import icet.koco.user.dto.UserCategoryStatProjection;
import icet.koco.user.dto.UserInfoResponseDto;
import icet.koco.user.entity.User;
import icet.koco.user.entity.UserAlgorithmStats;
import icet.koco.user.repository.UserAlgorithmStatsRepository;
import icet.koco.user.repository.UserRepository;
import icet.koco.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final KakaoOAuthClient kakaoOAuthClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final OAuthRepository oAuthRepository;
    private final CookieUtil cookieUtil;
    private final ProblemSetRepository problemSetRepository;
    private final SurveyRepository surveyRepository;
    private final UserAlgorithmStatsRepository userAlgorithmStatsRepository;
    private final UserAlgorithmStatsService userAlgorithmStatsService;


    /**
     * 유저 탈퇴
     * @param userId
     * @param response
     */
    @Transactional
    public void deleteUser(Long userId, HttpServletResponse response) {
        // 1. 유저 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("해당 유저 정보를 찾을 수 없습니다."));

        // 2. OAuth 정보 조회
        OAuth oauth = oAuthRepository.findByUserId(userId)
            .orElseThrow(() -> new UnauthorizedException("OAuth 정보가 존재하지 않습니다."));

        // 3. Kakao unlink 호출
        try {
            kakaoOAuthClient.unlinkUser(oauth.getProviderId());
        } catch (Exception e) {
            log.warn(">>>>> Kakao unlink 실패: {}", e.getMessage());
        }

        // 4. Redis refreshToken 삭제
        redisTemplate.delete(userId.toString());

        // 5. Soft delete 처리
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        // 6. 쿠키 삭제 처리
        cookieUtil.invalidateCookie(response, "access_token");
        cookieUtil.invalidateCookie(response, "refresh_token");
    }

    /**
     * 유저 정보 수정(업데이트)
     * @param userId
     * @param nickname
     * @param profileImgUrl
     * @param statusMsg
     */
    @Transactional
    public void updateUserInfo(Long userId, String nickname, String profileImgUrl, String statusMsg) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다"));

        String userName = user.getName();

        if (nickname != null) {
            user.setNickname(nickname);
        } else {
            user.setNickname(userName);
        }

        if (profileImgUrl != null) {
            user.setProfileImgUrl(profileImgUrl);
        } else {
            user.setProfileImgUrl(null);
        }

        if (statusMsg != null) {
            user.setStatusMsg(statusMsg);
        } else {
            user.setStatusMsg(null);
        }
        userRepository.save(user);
    }

    /**
     * 유저 정보 조회
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public UserInfoResponseDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        return UserInfoResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .statusMessage(user.getStatusMsg())
                .profileImageUrl(user.getProfileImgUrl())
                .build();
    }

    // userAlgorithmStats 조회 API
    @Transactional(readOnly = true)
    public UserAlgorithmStatsResponseDto getAlgorithmStats(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));

        List<UserAlgorithmStats> stats = userAlgorithmStatsRepository.findByUserId(userId);

        // 정답률 기준 내림차순 정렬 후 상위 5개만 추출
        List<UserAlgorithmStatsResponseDto.CategoryStat> topStats = stats.stream()
            .sorted(Comparator.comparingInt(UserAlgorithmStats::getCorrectRate).reversed())
            .limit(5)
            .map(s -> UserAlgorithmStatsResponseDto.CategoryStat.builder()
                .categoryId(s.getCategory().getId())
                .categoryName(s.getCategory().getName())
                .correctRate(s.getCorrectRate())
                .build())
            .toList();

        return UserAlgorithmStatsResponseDto.builder()
            .studyStats(topStats)
            .build();

    }

}

