package icet.koco.problemSet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProblem is a Querydsl query type for Problem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProblem extends EntityPathBase<Problem> {

    private static final long serialVersionUID = 1091874598L;

    public static final QProblem problem = new QProblem("problem");

    public final NumberPath<Integer> answerCnt = createNumber("answerCnt", Integer.class);

    public final StringPath bojUrl = createString("bojUrl");

    public final NumberPath<Integer> correctPplCnt = createNumber("correctPplCnt", Integer.class);

    public final NumberPath<Double> correctRate = createNumber("correctRate", Double.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath inputDescription = createString("inputDescription");

    public final StringPath inputExample = createString("inputExample");

    public final NumberPath<Integer> memoryLimit = createNumber("memoryLimit", Integer.class);

    public final NumberPath<Long> number = createNumber("number", Long.class);

    public final StringPath outputDescription = createString("outputDescription");

    public final StringPath outputExample = createString("outputExample");

    public final ListPath<ProblemCategory, QProblemCategory> problemCategories = this.<ProblemCategory, QProblemCategory>createList("problemCategories", ProblemCategory.class, QProblemCategory.class, PathInits.DIRECT2);

    public final ListPath<ProblemSetProblem, QProblemSetProblem> problemSetProblems = this.<ProblemSetProblem, QProblemSetProblem>createList("problemSetProblems", ProblemSetProblem.class, QProblemSetProblem.class, PathInits.DIRECT2);

    public final NumberPath<Integer> submissionCnt = createNumber("submissionCnt", Integer.class);

    public final StringPath tier = createString("tier");

    public final NumberPath<Integer> timeLimit = createNumber("timeLimit", Integer.class);

    public final StringPath title = createString("title");

    public QProblem(String variable) {
        super(Problem.class, forVariable(variable));
    }

    public QProblem(Path<? extends Problem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProblem(PathMetadata metadata) {
        super(Problem.class, metadata);
    }

}

