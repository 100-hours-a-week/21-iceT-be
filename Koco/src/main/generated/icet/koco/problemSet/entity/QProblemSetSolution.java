package icet.koco.problemSet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProblemSetSolution is a Querydsl query type for ProblemSetSolution
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProblemSetSolution extends EntityPathBase<ProblemSetSolution> {

    private static final long serialVersionUID = -775941899L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProblemSetSolution problemSetSolution = new QProblemSetSolution("problemSetSolution");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProblemSet problemSet;

    public final QSolution solution;

    public QProblemSetSolution(String variable) {
        this(ProblemSetSolution.class, forVariable(variable), INITS);
    }

    public QProblemSetSolution(Path<? extends ProblemSetSolution> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProblemSetSolution(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProblemSetSolution(PathMetadata metadata, PathInits inits) {
        this(ProblemSetSolution.class, metadata, inits);
    }

    public QProblemSetSolution(Class<? extends ProblemSetSolution> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.problemSet = inits.isInitialized("problemSet") ? new QProblemSet(forProperty("problemSet")) : null;
        this.solution = inits.isInitialized("solution") ? new QSolution(forProperty("solution"), inits.get("solution")) : null;
    }

}

