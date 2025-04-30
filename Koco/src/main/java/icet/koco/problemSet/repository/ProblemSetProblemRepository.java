package icet.koco.problemSet.repository;

import icet.koco.problemSet.entity.ProblemSetProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import icet.koco.problemSet.entity.ProblemSetProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemSetProblemRepository extends JpaRepository<ProblemSetProblem, Long> {

    // 문제집 ID로 문제 번호(ID)만 조회
    @Query("""
        SELECT p.id
        FROM ProblemSetProblem psp
        JOIN psp.problem p
        WHERE psp.problemSet.id = :problemSetId
    """)
    List<Long> findProblemIdsByProblemSetId(@Param("problemSetId") Long problemSetId);

}
