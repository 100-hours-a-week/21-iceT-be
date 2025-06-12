package icet.koco.problemSet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSolution is a Querydsl query type for Solution
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSolution extends EntityPathBase<Solution> {

    private static final long serialVersionUID = 1986195922L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSolution solution = new QSolution("solution");

    public final StringPath algorithm = createString("algorithm");

    public final StringPath codeCpp = createString("codeCpp");

    public final StringPath codeJava = createString("codeJava");

    public final StringPath codePy = createString("codePy");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProblem problem;

    public final ListPath<ProblemSetSolution, QProblemSetSolution> problemSetSolutions = this.<ProblemSetSolution, QProblemSetSolution>createList("problemSetSolutions", ProblemSetSolution.class, QProblemSetSolution.class, PathInits.DIRECT2);

    public final StringPath problemSolving = createString("problemSolving");

    public QSolution(String variable) {
        this(Solution.class, forVariable(variable), INITS);
    }

    public QSolution(Path<? extends Solution> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSolution(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSolution(PathMetadata metadata, PathInits inits) {
        this(Solution.class, metadata, inits);
    }

    public QSolution(Class<? extends Solution> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.problem = inits.isInitialized("problem") ? new QProblem(forProperty("problem")) : null;
    }

}

