package icet.koco.problemSet.service;

import icet.koco.global.exception.ResourceNotFoundException;
import icet.koco.problemSet.entity.Problem;
import icet.koco.problemSet.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemService {

	private final ProblemRepository problemRepository;

	public Problem findProblemByNumber(Long problemNumber) {
		return problemRepository.findByNumber(problemNumber)
			.orElseThrow(() -> new ResourceNotFoundException("해당 문제를 찾을 수 없습니다."));
	}
}
