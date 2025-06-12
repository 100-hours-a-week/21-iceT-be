package icet.koco.problemSet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProblemCategory is a Querydsl query type for ProblemCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProblemCategory extends EntityPathBase<ProblemCategory> {

    private static final long serialVersionUID = -1107604924L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProblemCategory problemCategory = new QProblemCategory("problemCategory");

    public final QCategory category;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProblem problem;

    public QProblemCategory(String variable) {
        this(ProblemCategory.class, forVariable(variable), INITS);
    }

    public QProblemCategory(Path<? extends ProblemCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProblemCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProblemCategory(PathMetadata metadata, PathInits inits) {
        this(ProblemCategory.class, metadata, inits);
    }

    public QProblemCategory(Class<? extends ProblemCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
        this.problem = inits.isInitialized("problem") ? new QProblem(forProperty("problem")) : null;
    }

}

