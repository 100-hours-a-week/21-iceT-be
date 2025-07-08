package icet.koco.problemRecommendation.repository;

import icet.koco.problemRecommendation.entity.ProblemRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRecommendationRepository extends JpaRepository<ProblemRecommendation, Long> {
}
