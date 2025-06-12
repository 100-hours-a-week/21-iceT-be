package icet.koco.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserDetail is a Querydsl query type for UserDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserDetail extends EntityPathBase<UserDetail> {

    private static final long serialVersionUID = 1789418845L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserDetail userDetail = new QUserDetail("userDetail");

    public final QCluster cluster;

    public final DateTimePath<java.time.LocalDateTime> clusterTime = createDateTime("clusterTime", java.time.LocalDateTime.class);

    public final NumberPath<Integer> correctCnt = createNumber("correctCnt", Integer.class);

    public final NumberPath<Double> correctRate = createNumber("correctRate", Double.class);

    public final NumberPath<Double> difficultyAvg = createNumber("difficultyAvg", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> responseRate = createNumber("responseRate", Double.class);

    public final NumberPath<Integer> surveyCnt = createNumber("surveyCnt", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final QUser user;

    public QUserDetail(String variable) {
        this(UserDetail.class, forVariable(variable), INITS);
    }

    public QUserDetail(Path<? extends UserDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserDetail(PathMetadata metadata, PathInits inits) {
        this(UserDetail.class, metadata, inits);
    }

    public QUserDetail(Class<? extends UserDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.cluster = inits.isInitialized("cluster") ? new QCluster(forProperty("cluster")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

