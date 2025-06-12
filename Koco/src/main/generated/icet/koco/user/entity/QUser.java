package icet.koco.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -2124842516L;

    public static final QUser user = new QUser("user");

    public final ListPath<icet.koco.posts.entity.Comment, icet.koco.posts.entity.QComment> comments = this.<icet.koco.posts.entity.Comment, icet.koco.posts.entity.QComment>createList("comments", icet.koco.posts.entity.Comment.class, icet.koco.posts.entity.QComment.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<icet.koco.posts.entity.Like, icet.koco.posts.entity.QLike> likes = this.<icet.koco.posts.entity.Like, icet.koco.posts.entity.QLike>createList("likes", icet.koco.posts.entity.Like.class, icet.koco.posts.entity.QLike.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final ListPath<icet.koco.posts.entity.Post, icet.koco.posts.entity.QPost> posts = this.<icet.koco.posts.entity.Post, icet.koco.posts.entity.QPost>createList("posts", icet.koco.posts.entity.Post.class, icet.koco.posts.entity.QPost.class, PathInits.DIRECT2);

    public final StringPath profileImgUrl = createString("profileImgUrl");

    public final EnumPath<icet.koco.enums.UserRole> role = createEnum("role", icet.koco.enums.UserRole.class);

    public final StringPath statusMsg = createString("statusMsg");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

