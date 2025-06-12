package icet.koco.posts.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostCategory is a Querydsl query type for PostCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostCategory extends EntityPathBase<PostCategory> {

    private static final long serialVersionUID = 123382549L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostCategory postCategory = new QPostCategory("postCategory");

    public final icet.koco.problemSet.entity.QCategory category;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPost post;

    public QPostCategory(String variable) {
        this(PostCategory.class, forVariable(variable), INITS);
    }

    public QPostCategory(Path<? extends PostCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostCategory(PathMetadata metadata, PathInits inits) {
        this(PostCategory.class, metadata, inits);
    }

    public QPostCategory(Class<? extends PostCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new icet.koco.problemSet.entity.QCategory(forProperty("category")) : null;
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
    }

}

