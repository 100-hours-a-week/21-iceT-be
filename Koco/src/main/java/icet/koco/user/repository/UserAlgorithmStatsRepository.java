package icet.koco.user.repository;

import icet.koco.user.entity.User;
import icet.koco.user.entity.UserAlgorithmStats;
import icet.koco.problemSet.entity.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAlgorithmStatsRepository extends JpaRepository<UserAlgorithmStats, Long> {

    // 특정 사용자에 대한 전체 알고리즘 통계 조회
    List<UserAlgorithmStats> findByUser(User user);

    // 특정 사용자 + 카테고리 통계 조회
    Optional<UserAlgorithmStats> findByUserAndCategory(User user, Category category);
}
