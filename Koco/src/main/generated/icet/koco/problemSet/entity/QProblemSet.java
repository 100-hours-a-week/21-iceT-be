package icet.koco.problemSet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProblemSet is a Querydsl query type for ProblemSet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProblemSet extends EntityPathBase<ProblemSet> {

    private static final long serialVersionUID = -2046067876L;

    public static final QProblemSet problemSet = new QProblemSet("problemSet");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<ProblemSetProblem, QProblemSetProblem> problemSetProblems = this.<ProblemSetProblem, QProblemSetProblem>createList("problemSetProblems", ProblemSetProblem.class, QProblemSetProblem.class, PathInits.DIRECT2);

    public final ListPath<ProblemSetSolution, QProblemSetSolution> problemSetSolutions = this.<ProblemSetSolution, QProblemSetSolution>createList("problemSetSolutions", ProblemSetSolution.class, QProblemSetSolution.class, PathInits.DIRECT2);

    public QProblemSet(String variable) {
        super(ProblemSet.class, forVariable(variable));
    }

    public QProblemSet(Path<? extends ProblemSet> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProblemSet(PathMetadata metadata) {
        super(ProblemSet.class, metadata);
    }

}

