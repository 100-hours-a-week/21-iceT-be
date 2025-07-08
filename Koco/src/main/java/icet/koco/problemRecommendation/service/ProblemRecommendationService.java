package icet.koco.problemRecommendation.service;

import icet.koco.problemRecommendation.dto.RecommendationRequestDto;
import icet.koco.problemRecommendation.entity.ProblemRecommendation;
import icet.koco.problemRecommendation.repository.ProblemRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemRecommendationService {

	private final ProblemRecommendationRepository problemRecommendationRepository;

	public void saveRecommendations(RecommendationRequestDto dto) {
		List<ProblemRecommendation> recommendations = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();
		ProblemRecommendation.Type[] types = ProblemRecommendation.Type.values(); // TT, FT, TF, FF

		for (int i = 0; i < dto.getRecommendations().size() && i < types.length; i++) {
			List<Long> pair = dto.getRecommendations().get(i);

			if (pair.size() != 2) continue;

			ProblemRecommendation rec = ProblemRecommendation.builder()
				.type(types[i]) // TT → FT → TF → FF 순서 보장
				.problem1Id(pair.get(0))
				.problem2Id(pair.get(1))
				.createdAt(now)
				.build();

			recommendations.add(rec);
		}

		problemRecommendationRepository.saveAll(recommendations);
	}
}
