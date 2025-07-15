package icet.koco.posts.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeCountCacheService {
	private final RedisTemplate<String, Object> redisTemplate;

	private String getKey(Long postId) {
		return "post:likeCount:" + postId;
	}

	public Integer getLikeCount(Long postId) {
		String key = getKey(postId);
		Object value = redisTemplate.opsForValue().get(key);
		if (value instanceof Integer) return (Integer) value;
		if (value instanceof String) return Integer.valueOf((String) value);
		return null;
	}

	public void setLikeCount(Long postId, Integer count, Duration ttl) {
		redisTemplate.opsForValue().set(getKey(postId), count, ttl);
	}

	public void increment(Long postId) {
		redisTemplate.opsForValue().increment(getKey(postId));
	}

	public void decrement(Long postId) {
		redisTemplate.opsForValue().decrement(getKey(postId));
	}

	public void delete(Long postId) {
		redisTemplate.delete(getKey(postId));
	}
}
