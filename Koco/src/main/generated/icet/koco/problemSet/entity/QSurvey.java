package icet.koco.problemSet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSurvey is a Querydsl query type for Survey
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSurvey extends EntityPathBase<Survey> {

    private static final long serialVersionUID = 1509461491L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSurvey survey = new QSurvey("survey");

    public final DateTimePath<java.time.LocalDateTime> answeredAt = createDateTime("answeredAt", java.time.LocalDateTime.class);

    public final EnumPath<icet.koco.enums.DifficultyLevel> difficultyLevel = createEnum("difficultyLevel", icet.koco.enums.DifficultyLevel.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isSolved = createBoolean("isSolved");

    public final QProblem problem;

    public final QProblemSet problemSet;

    public final icet.koco.user.entity.QUser user;

    public QSurvey(String variable) {
        this(Survey.class, forVariable(variable), INITS);
    }

    public QSurvey(Path<? extends Survey> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSurvey(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSurvey(PathMetadata metadata, PathInits inits) {
        this(Survey.class, metadata, inits);
    }

    public QSurvey(Class<? extends Survey> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.problem = inits.isInitialized("problem") ? new QProblem(forProperty("problem")) : null;
        this.problemSet = inits.isInitialized("problemSet") ? new QProblemSet(forProperty("problemSet")) : null;
        this.user = inits.isInitialized("user") ? new icet.koco.user.entity.QUser(forProperty("user")) : null;
    }

}

