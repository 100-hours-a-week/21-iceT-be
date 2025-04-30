package icet.koco.problemSet.repository;

import icet.koco.problemSet.entity.Problem;
import icet.koco.problemSet.entity.Solution;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
    // 문제로 조회
    // problem Entity를 넘기면 내부에서 problem.getId()로 처리해준다고 함
    Optional<Solution> findByProblem(Problem problem);
}
