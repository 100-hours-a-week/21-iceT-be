package icet.koco.posts.service;

import icet.koco.alarm.dto.AlarmRequestDto;
import icet.koco.alarm.service.AlarmService;
import icet.koco.enums.AlarmType;
import icet.koco.enums.ErrorMessage;
import icet.koco.global.exception.AlreadyLikedException;
import icet.koco.global.exception.ForbiddenException;
import icet.koco.global.exception.ResourceNotFoundException;
import icet.koco.posts.dto.like.LikeResponseDto;
import icet.koco.posts.entity.Like;
import icet.koco.posts.entity.Post;
import icet.koco.posts.repository.LikeRepository;
import icet.koco.posts.repository.PostRepository;
import icet.koco.user.entity.User;
import icet.koco.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AlarmService alarmService;

	private final LikeCountCacheService likeCountCacheService;

    @Transactional
    public LikeResponseDto createLike(Long userId, Long postId) {
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
			log.warn("유저 {}가 이미 좋아요 한 게시글 {}에 중복 요청", userId, postId);
            throw new AlreadyLikedException(ErrorMessage.ALREADY_LIKED_ERROR);
        }

		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

		Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.POST_NOT_FOUND));

        Like like = Like.builder()
            .post(post)
            .user(user)
            .createdAt(LocalDateTime.now())
            .build();
        likeRepository.save(like);
		log.info("유저 {}가 게시글 {}에 좋아요를 생성", userId, postId);

        if (!post.getUser().getId().equals(user.getId())) {
            AlarmRequestDto alarmRequestDto = AlarmRequestDto.builder()
                .postId(post.getId())
                .senderId(user.getId())
                .alarmType(AlarmType.LIKE)
                .build();
			log.info("게시글 {}의 작성자 {}에게 좋아요 알림 전송", postId, post.getUser().getId());
            alarmService.createAlarmInternal(alarmRequestDto);
        }

		// Redis 증가
		likeCountCacheService.increment(postId);
		log.info("Redis likeCount 증가 완료 - postId: {}", postId);

        // 낙관적 락으로 likeCount 증가 시도
        boolean success = false;
        int retry = 0;
        while (!success && retry < 3) {
            try {
                post.setLikeCount(post.getLikeCount() + 1);
                postRepository.save(post);
                success = true;
				log.info("DB likeCount 증가 성공 - postId: {}, 시도: {}", postId, retry + 1);
            } catch (ObjectOptimisticLockingFailureException e) {
                retry++;
				log.warn("낙관적 락 충돌 - postId: {}, 재시도: {}", postId, retry);
                post = postRepository.findByIdAndDeletedAtIsNull(postId).orElseThrow();
            }
        }

        if (!success) {
			likeCountCacheService.decrement(postId);
			log.error("DB likeCount 증가 실패 - postId: {}, rollback Redis 완료", postId);
            throw new RuntimeException(ErrorMessage.LIKE_CONCURRENCY_FAILURE.getMessage());
        }

        return LikeResponseDto.builder()
            .postId(postId)
            .liked(true)
            .likeCount(post.getLikeCount())
            .build();
    }

    @Transactional
    public void deleteLike(Long userId, Long postId) {
        // 사용자, 게시글 존재 확인
		userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

		Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.POST_NOT_FOUND));


        // 좋아요 취소 확인
        if (!likeRepository.existsByUserIdAndPostId(userId, postId)) {
			log.warn("유저 {}가 좋아요하지 않은 게시글 {}에 대해 삭제 요청", userId, postId);
            throw new AlreadyLikedException(ErrorMessage.ALREADY_UNLIKED_ERROR);
        }

        // 좋아요 조회
        Like like = likeRepository.findByUserIdAndPostId(userId, postId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.LIKE_NOT_FOUND));

        // 본인 확인
        if (!like.getUser().getId().equals(userId)) {
			log.warn("유저 {}가 본인이 누르지 않은 좋아요 삭제 시도 - likeId: {}", userId, like.getId());
            throw new ForbiddenException(ErrorMessage.NO_LIKE_PERMISSION);
        }

        // 좋아요 삭제
        likeRepository.delete(like);
		log.info("유저 {}가 게시글 {}에 대해 좋아요 취소", userId, postId);

		// Redis
		likeCountCacheService.decrement(postId);
		log.debug("Redis likeCount 감소 완료 - postId: {}", postId);

        // 낙관적 락으로 likeCount 감소 (retry 3번)
        boolean success = false;
        int retry = 0;
        while (!success && retry < 3) {
            try {
                post.setLikeCount(post.getLikeCount() - 1);
                postRepository.save(post);
                success = true;
				log.info("DB likeCount 감소 성공 - postId: {}, 시도: {}", postId, retry + 1);
            } catch (ObjectOptimisticLockingFailureException e) {
                retry++;
				log.warn("낙관적 락 충돌 - postId: {}, 재시도: {}", postId, retry);
                postRepository.findByIdAndDeletedAtIsNull(postId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.POST_NOT_FOUND));
            }
        }

        if (!success) {
			likeCountCacheService.increment(postId);
			log.error("DB likeCount 감소 실패 - postId: {}, rollback Redis 완료", postId);
            throw new RuntimeException(ErrorMessage.LIKE_CONCURRENCY_FAILURE.getMessage());
        }
    }
}
