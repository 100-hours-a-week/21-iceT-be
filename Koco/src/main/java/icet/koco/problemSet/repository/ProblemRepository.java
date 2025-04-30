package icet.koco.problemSet.repository;

import icet.koco.problemSet.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

}
