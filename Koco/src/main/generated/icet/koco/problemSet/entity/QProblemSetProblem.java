package icet.koco.problemSet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProblemSetProblem is a Querydsl query type for ProblemSetProblem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProblemSetProblem extends EntityPathBase<ProblemSetProblem> {

    private static final long serialVersionUID = 2111152035L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProblemSetProblem problemSetProblem = new QProblemSetProblem("problemSetProblem");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProblem problem;

    public final QProblemSet problemSet;

    public QProblemSetProblem(String variable) {
        this(ProblemSetProblem.class, forVariable(variable), INITS);
    }

    public QProblemSetProblem(Path<? extends ProblemSetProblem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProblemSetProblem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProblemSetProblem(PathMetadata metadata, PathInits inits) {
        this(ProblemSetProblem.class, metadata, inits);
    }

    public QProblemSetProblem(Class<? extends ProblemSetProblem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.problem = inits.isInitialized("problem") ? new QProblem(forProperty("problem")) : null;
        this.problemSet = inits.isInitialized("problemSet") ? new QProblemSet(forProperty("problemSet")) : null;
    }

}

