package icet.koco.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserAlgorithmStats is a Querydsl query type for UserAlgorithmStats
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAlgorithmStats extends EntityPathBase<UserAlgorithmStats> {

    private static final long serialVersionUID = -999291268L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserAlgorithmStats userAlgorithmStats = new QUserAlgorithmStats("userAlgorithmStats");

    public final icet.koco.problemSet.entity.QCategory category;

    public final NumberPath<Integer> correctRate = createNumber("correctRate", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QUser user;

    public QUserAlgorithmStats(String variable) {
        this(UserAlgorithmStats.class, forVariable(variable), INITS);
    }

    public QUserAlgorithmStats(Path<? extends UserAlgorithmStats> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserAlgorithmStats(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserAlgorithmStats(PathMetadata metadata, PathInits inits) {
        this(UserAlgorithmStats.class, metadata, inits);
    }

    public QUserAlgorithmStats(Class<? extends UserAlgorithmStats> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new icet.koco.problemSet.entity.QCategory(forProperty("category")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

