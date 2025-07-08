package icet.koco.problemRecommendation.entity;

import icet.koco.problemSet.entity.ProblemSet;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "problem_recommendation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProblemRecommendation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private Type type; // "TT", "FT", "TF", "FF"

	@Column(name = "problem1_id", nullable = false)
	private Long problem1Id;

	@Column(name = "problem2_id", nullable = false)
	private Long problem2Id;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	public enum Type {
		TT, FT, TF, FF
	}
}
