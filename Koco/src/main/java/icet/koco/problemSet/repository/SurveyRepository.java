package icet.koco.problemSet.repository;

import icet.koco.problemSet.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    @Query("""
        SELECT COUNT(s) = 0
        FROM Survey s
        WHERE s.user.id = :userId
          AND s.problemSet.id = :problemSetId
          AND s.answeredAt IS NULL
    """)
    boolean isAllAnsweredByUserAndProblemSet(
        @Param("userId") Long userId,
        @Param("problemSetId") Long problemSetId
    );
}