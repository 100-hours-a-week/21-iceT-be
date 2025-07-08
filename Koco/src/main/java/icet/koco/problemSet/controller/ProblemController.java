package icet.koco.problemSet.controller;

import icet.koco.enums.ApiResponseCode;
import icet.koco.global.dto.ApiResponse;
import icet.koco.problemSet.dto.PreviousProblemListResponseDto;
import icet.koco.problemSet.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/backend/v1/problems")
public class ProblemController {
	private final ProblemService problemService;

	@Operation(summary = "전날 문제 리스트 정보를 가져오는 API입니다.")
	@GetMapping("/previous")
	public ResponseEntity<?> getPreviousProblemInfo() {

		PreviousProblemListResponseDto responseDto = problemService.getPreviousProblemInfo();

		return ResponseEntity.ok(ApiResponse.success(ApiResponseCode.PREVIOUS_PROBLEM_FETCH_SUCCESS, "전날 문제 정보를 가져오는데 성공했습니다.", responseDto));
	}
}
