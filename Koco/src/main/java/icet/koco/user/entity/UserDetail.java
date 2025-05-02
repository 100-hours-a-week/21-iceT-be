package icet.koco.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_detail")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // 회원 ID
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // 군집
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id", nullable = false)
    private Cluster cluster;

    // 군집에 등록된 날짜
    @Column(name = "cluster_time", nullable = false)
    private LocalDateTime clusterTime;

    // 맞은 문제 개수
    @Column(name = "correct_cnt", nullable = false)
    private Integer correctCnt;

    // 응답 설문 개수
    @Column(name = "survey_cnt", nullable = false)
    private Integer surveyCnt;

    // 평균 난이도
    @Column(name = "difficulty_avg", nullable = false, columnDefinition = "DECIMAL(4,1)")
    private Double difficultyAvg;

    // 맞춘 비율
    @Column(name = "correct_rate", nullable = false, columnDefinition = "DECIMAL(4, 1)")
    private Double correctRate;

    // 응답 비율
    @Column(name = "response_rate", nullable = false, columnDefinition = "DECIMAL(4, 1)")
    private Double responseRate;

    // 전체 출석일수
    @Column(name = "total_attendance", nullable = false)
    private Integer totalAttendance;

    // 연속 출석일수
    @Column(name = "continuous_attendance", nullable = false)
    private Integer continuousAttendance;

    // 갱신일자
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
