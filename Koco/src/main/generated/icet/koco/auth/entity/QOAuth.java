package icet.koco.auth.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOAuth is a Querydsl query type for OAuth
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOAuth extends EntityPathBase<OAuth> {

    private static final long serialVersionUID = -1871022599L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOAuth oAuth = new QOAuth("oAuth");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath provider = createString("provider");

    public final StringPath providerId = createString("providerId");

    public final StringPath refreshToken = createString("refreshToken");

    public final icet.koco.user.entity.QUser user;

    public QOAuth(String variable) {
        this(OAuth.class, forVariable(variable), INITS);
    }

    public QOAuth(Path<? extends OAuth> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOAuth(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOAuth(PathMetadata metadata, PathInits inits) {
        this(OAuth.class, metadata, inits);
    }

    public QOAuth(Class<? extends OAuth> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new icet.koco.user.entity.QUser(forProperty("user")) : null;
    }

}

