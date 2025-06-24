package icet.koco.problemSet.service;

import icet.koco.enums.ErrorMessage;
import icet.koco.global.exception.ResourceNotFoundException;
import icet.koco.problemSet.dto.AiSolutionRequestDto;
import icet.koco.problemSet.entity.*;
import icet.koco.problemSet.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final ProblemRepository problemRepository;
	private final ProblemSetRepository problemSetRepository;
	private final ProblemSetProblemRepository problemSetProblemRepository;
	private final ProblemSetSolutionRespository problemSetSolutionRepository;
    /**
     * AI 서버로부터 해설 받아오기
     * @param aiSolutionRequestDto
     */
    public void saveFromAi(AiSolutionRequestDto aiSolutionRequestDto) {
        // 백준 번호로 내부 problemId 조회
        Problem problem = problemRepository.findByNumber(aiSolutionRequestDto.getProblemNumber())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PROBLEM_NOT_FOUND));

        // 문제에 대한 해설이 이미 존재하는지 확인
        if (solutionRepository.existsByProblem(problem)) {
            throw new IllegalArgumentException(ErrorMessage.ALREADY_SOLUTION_EXIST.getMessage());
        }

        // Solution 저장
        Solution solution = Solution.builder()
            .problem(problem)  // 연관관계 설정
            .description(aiSolutionRequestDto.getProblem_check().getProblem_description())
            .algorithm(aiSolutionRequestDto.getProblem_check().getAlgorithm())
            .problemSolving(aiSolutionRequestDto.getProblem_solving())
            .codeCpp(aiSolutionRequestDto.getSolution_code().getCpp())
            .codeJava(aiSolutionRequestDto.getSolution_code().getJava())
            .codePy(aiSolutionRequestDto.getSolution_code().getPython())
            .createdAt(LocalDateTime.now())
            .build();

        solutionRepository.save(solution);

		// 오늘 날짜 문제집 찾거나 생성
		LocalDate today = LocalDate.now();
		ProblemSet problemSet = problemSetRepository.findByCreatedAt(today)
			.orElseGet(() -> {
				ProblemSet newSet = ProblemSet.builder()
					.createdAt(today)
					.build();
				return problemSetRepository.save(newSet);
			});

		// 5. problem_set_problem 매핑
		boolean alreadyMappedProblem = problemSetProblemRepository.existsByProblemSetAndProblem(problemSet, problem);
		if (!alreadyMappedProblem) {
			ProblemSetProblem problemSetProblem = ProblemSetProblem.builder()
				.problemSet(problemSet)
				.problem(problem)
				.build();
			problemSetProblemRepository.save(problemSetProblem);
		}

		// problem_set_solution 매핑
		boolean alreadyMappedSolution = problemSetSolutionRepository.existsByProblemSetAndSolution(problemSet, solution);
		if (!alreadyMappedSolution) {
			ProblemSetSolution problemSetSolution = ProblemSetSolution.builder()
				.problemSet(problemSet)
				.solution(solution)
				.build();
			problemSetSolutionRepository.save(problemSetSolution);
		}
    }
}
