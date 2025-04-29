package icet.koco.problemSet.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solution")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // 문제 연관
    @OneToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    // 문제 해설 본문
    @Column(name = "explanation", nullable = false, columnDefinition = "TEXT")
    private String explanation;

    // C++ 코드
    @Column(name = "code_cpp", nullable = false, columnDefinition = "TEXT")
    private String codeCpp;

    // Java 코드
    @Column(name = "code_java", nullable = false, columnDefinition = "TEXT")
    private String codeJava;

    // Python 코드
    @Column(name = "code_py", nullable = false, columnDefinition = "TEXT")
    private String codePy;

    // 생성 일자
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "solution")
    private List<ProblemSetSolution> problemSetSolutions = new ArrayList<>();
}
