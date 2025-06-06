package icet.koco.posts.service;

import static java.time.LocalDateTime.now;

import icet.koco.global.exception.ForbiddenException;
import icet.koco.global.exception.ResourceNotFoundException;
import icet.koco.global.exception.UnauthorizedException;
import icet.koco.posts.dto.PostCreateEditRequestDto;
import icet.koco.posts.dto.PostCreateResponseDto;
import icet.koco.posts.dto.PostGetDetailResponseDto;
import icet.koco.posts.entity.Post;
import icet.koco.posts.entity.PostCategory;
import icet.koco.posts.repository.CommentRepository;
import icet.koco.posts.repository.LikeRepository;
import icet.koco.posts.repository.PostCategoryRepository;
import icet.koco.posts.repository.PostRepository;
import icet.koco.problemSet.entity.Category;
import icet.koco.problemSet.repository.CategoryRepository;
import icet.koco.problemSet.repository.ProblemRepository;
import icet.koco.user.entity.User;
import icet.koco.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final ProblemRepository problemRepository;

    /**
     * 게시물 등록
     * @param userId 유저Id
     * @param requestDto (number, title, content, category)
     * @return postId
     */
    @Transactional
    public PostCreateResponseDto createPost(Long userId, PostCreateEditRequestDto requestDto) {
        // 유저 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ForbiddenException("존재하지 않는 사용자입니다."));

        // 해당 문제가 존재하는지 확인
        problemRepository.findByNumber(requestDto.getProblemNumber())
            .orElseThrow(() -> new IllegalArgumentException(
                "해당 문제 번호를 가진 Problem이 없습니다: " + requestDto.getProblemNumber()));

        // Post Entity 생성
        Post post = Post.builder()
            .user(user)
            .problemNumber(requestDto.getProblemNumber())
            .commentCount(0)
            .likeCount(0)
            .content(requestDto.getContent())
            .title(requestDto.getTitle())
            .createdAt(now())
            .build();

        // 카테고리 이름으로 Category 조회
        List<Category> categories = categoryRepository.findByNameIn(requestDto.getCategory());

        if (categories.size() != requestDto.getCategory().size()) {
            throw new IllegalArgumentException("존재하지 않는 카테고리가 포함되어 있습니다.");
        }

        // 중간테이블에 카테고리 매핑
        for (Category category : categories) {
            PostCategory postCategory = PostCategory.builder()
                .category(category)
                .build();
            post.addPostCategory(postCategory);
        }

        // 저장
        postRepository.save(post);

        // DTO에 담아 반환
        return PostCreateResponseDto.builder()
            .postId(post.getId())
            .build();
    }

    /**
     * 게시글 상세 조회
     * @param postId 게시글 Id
     * @return PostGetDetailResponseDto
     */
    @Transactional(readOnly = true)
    public PostGetDetailResponseDto getPost(Long postId) {
        // 게시글 찾기
        Post post = postRepository.findByIdWithUserAndCategories(postId)
            .orElseThrow(() -> new ForbiddenException("해당 게시글이 존재하지 않습니다."));

        // 댓글, 좋아요 수
        Integer likeCount = likeRepository.countByPostId(postId);
        Integer commentCount = commentRepository.countByPostId(postId);

        // DTO에 맞춰서 반환
        return PostGetDetailResponseDto.builder()
            .postId(postId)
            .title(post.getTitle())
            .categories(
                post.getPostCategories().stream()
                    .map(c -> new PostGetDetailResponseDto.CategoryDto(c.getCategory().getId(), c.getCategory().getName()))
                    .toList()
            )
            .content(post.getContent())
            .author(PostGetDetailResponseDto.AuthorDto.builder()
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .imgUrl(post.getUser().getProfileImgUrl())
                .build())
            .commentCount(commentCount)
            .likeCount(likeCount)
            .build();
    }

    /**
     * 게시글 수정
     * @param userId 로그인된 유저의 Id
     * @param postId 게시물 Id
     * @param requestDto (problemNumber, title, content, categories)
     */
    @Transactional
    public void editPost(Long userId, Long postId, PostCreateEditRequestDto requestDto) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> {
                return new ResourceNotFoundException("해당 게시글이 존재하지 않습니다.");
            });

        // 권한 체크
        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("게시글 수정 권한이 없습니다.");
        }

        // 내용 저장
        if (requestDto.getProblemNumber() != null) {
            // 해당 문제가 존재하는지 확인
            problemRepository.findByNumber(requestDto.getProblemNumber())
                .orElseThrow(() -> new IllegalArgumentException(
                    "해당 문제 번호를 가진 Problem이 없습니다: " + requestDto.getProblemNumber()));
            post.setProblemNumber(requestDto.getProblemNumber());

        }

        if (requestDto.getTitle() != null) {
            post.setTitle(requestDto.getTitle());
        }

        if (requestDto.getContent() != null) {
            post.setContent(requestDto.getContent());
        }

        if (requestDto.getCategory() != null) {
            post.getPostCategories().clear();
            postCategoryRepository.deleteByPost(post);

            // 카테고리 이름으로 Category 조회
            List<Category> categories = categoryRepository.findByNameIn(requestDto.getCategory());

            if (categories.size() != requestDto.getCategory().size()) {
                throw new IllegalArgumentException("존재하지 않는 카테고리가 포함되어 있습니다.");
            }

            // 중간테이블에 카테고리 매핑
            for (Category category : categories) {
                PostCategory postCategory = PostCategory.builder()
                    .category(category)
                    .build();
                post.addPostCategory(postCategory);
            }
        }

        // 수정된 시간 저장
        post.setUpdatedAt(now());

    }
}
