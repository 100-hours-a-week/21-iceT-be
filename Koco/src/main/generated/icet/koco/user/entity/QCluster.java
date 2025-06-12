package icet.koco.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCluster is a Querydsl query type for Cluster
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCluster extends EntityPathBase<Cluster> {

    private static final long serialVersionUID = -936065991L;

    public static final QCluster cluster = new QCluster("cluster");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QCluster(String variable) {
        super(Cluster.class, forVariable(variable));
    }

    public QCluster(Path<? extends Cluster> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCluster(PathMetadata metadata) {
        super(Cluster.class, metadata);
    }

}

