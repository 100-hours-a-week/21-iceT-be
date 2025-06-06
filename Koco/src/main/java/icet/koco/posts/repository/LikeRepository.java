package icet.koco.posts.repository;

import icet.koco.posts.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Integer countByPostId(Long postId);

}
