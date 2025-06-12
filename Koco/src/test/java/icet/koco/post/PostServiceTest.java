package icet.koco.post;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

import icet.koco.global.exception.ResourceNotFoundException;
import icet.koco.global.exception.UnauthorizedException;
import icet.koco.posts.dto.post.PostCreateEditRequestDto;
import icet.koco.posts.dto.post.PostCreateResponseDto;
import icet.koco.posts.dto.post.PostGetDetailResponseDto;
import icet.koco.posts.entity.Post;
import icet.koco.posts.entity.PostCategory;
import icet.koco.problemSet.entity.Category;
import icet.koco.problemSet.entity.Problem;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import icet.koco.posts.repository.CommentRepository;
import icet.koco.posts.repository.LikeRepository;
import icet.koco.posts.repository.PostCategoryRepository;
import icet.koco.posts.repository.PostRepository;
import icet.koco.posts.service.PostService;
import icet.koco.problemSet.repository.CategoryRepository;
import icet.koco.problemSet.repository.ProblemRepository;
import icet.koco.user.entity.User;
import icet.koco.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private PostCategoryRepository postCategoryRepository;

    Long userId = 1L;
    Long postId = 100L;

    private User user;
    private Post post;
    private Category category;
    private Problem problem;
    private PostCreateEditRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(userId)
            .nickname("테스트 유저")
            .profileImgUrl("img.png")
            .build();

        category = Category.builder()
            .id(1L)
            .name("dp")
            .build();

        PostCategory postCategory = PostCategory.builder()
            .category(category)
            .build();

        post = Post.builder()
            .id(postId)
            .user(user)
            .problemNumber(30000L)
            .title("테스트 제목")
            .content("테스트 내용")
            .commentCount(5)
            .likeCount(5)
            .createdAt(LocalDateTime.of(2025, 6, 10, 12, 0))
            .build();

        post.addPostCategory(postCategory);

        problem = Problem.builder()
            .id(1L)
            .number(30000L)
            .build();

        requestDto = PostCreateEditRequestDto.builder()
            .problemNumber(30000L)
            .title("테스트 제목")
            .content("테스트 내용")
            .category(List.of("dp"))
            .build();

    }


    @Nested
    @DisplayName("게시글 생성")
    class createPostTest {
        @Test
        void createPost_성공() {
            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(problemRepository.findByNumber(30000L)).willReturn(Optional.of(problem));
            given(categoryRepository.findByNameIn(List.of("dp"))).willReturn(List.of(category));

            // when
            PostCreateResponseDto responseDto = postService.createPost(userId, requestDto);

            // then
            assertThat(responseDto).isNotNull();

            ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
            then(postRepository).should().save(postCaptor.capture());

            Post savedPost = postCaptor.getValue();
            assertThat(savedPost.getTitle()).isEqualTo("테스트 제목");
            assertThat(savedPost.getContent()).isEqualTo("테스트 내용");
            assertThat(savedPost.getUser()).isEqualTo(user);
            assertThat(savedPost.getProblemNumber()).isEqualTo(30000L);
            assertThat(savedPost.getPostCategories().size()).isEqualTo(1);
            assertThat(savedPost.getPostCategories().get(0).getCategory().getName()).isEqualTo("dp");

        }

        @Test
        void createPost_존재하지않는사용자() {
            given(userRepository.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postService.createPost(userId, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("존재하지 않는 사용자입니다.");
        }

        @Test
        void createPost_존재하지않는문제번호() {
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(problemRepository.findByNumber(30000L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postService.createPost(userId, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("해당 문제 번호를 가진 Problem이 없습니다.");
        }

        @Test
        void createPost_존재하지않는카테고리() {
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(problemRepository.findByNumber(30000L)).willReturn(Optional.of(problem));
            given(categoryRepository.findByNameIn(List.of("dp"))).willReturn(List.of());

            // when & then
            assertThatThrownBy(() -> postService.createPost(userId, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("존재하지 않는 카테고리가 포함되어 있습니다.");
        }
    }

    @Nested
    @DisplayName("게시글 조회")
    class getPostTest {

        @Test
        void getPost_성공() {
            // given
            given(postRepository.findByIdWithUserAndCategories(postId)).willReturn(Optional.of(post));
            given(likeRepository.countByPostId(postId)).willReturn(5);
            given(commentRepository.countByPostIdAndDeletedAtIsNull(postId)).willReturn(5);
            given(likeRepository.existsByUserIdAndPostId(userId, postId)).willReturn(false);

            // when
            PostGetDetailResponseDto responseDto = postService.getPost(userId, postId);

            // then
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getPostId()).isEqualTo(postId);
            assertThat(responseDto.getTitle()).isEqualTo("테스트 제목");
            assertThat(responseDto.getContent()).isEqualTo("테스트 내용");
            assertThat(responseDto.getLikeCount()).isEqualTo(5);
            assertThat(responseDto.getCommentCount()).isEqualTo(5);
            assertThat(responseDto.isLiked()).isFalse();
            assertThat(responseDto.getCategories().size()).isEqualTo(1);
            assertThat(responseDto.getCategories().get(0).getCategoryName()).isEqualTo("dp");
            assertThat(responseDto.getAuthor().getUserId()).isEqualTo(userId);
            assertThat(responseDto.getAuthor().getNickname()).isEqualTo("테스트 유저");
        }

        @Test
        void getPost_존재하지않는게시글() {
            // given
            given(postRepository.findByIdWithUserAndCategories(postId)).willReturn(Optional.empty());

            // then & when
            assertThatThrownBy(() -> postService.getPost(userId, postId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("해당 게시글이 존재하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("게시글 수정")
    class editPostTest {
        @Test
        @DisplayName("게시글 수정 성공")
        void editPost_성공() {
            // given
            PostCreateEditRequestDto requestDto = PostCreateEditRequestDto.builder()
                .problemNumber(2000L)
                .title("수정된 제목")
                .content("수정된 내용")
                .category(List.of("dp"))
                .build();

            given(postRepository.findByIdWithUserAndCategories(postId)).willReturn(Optional.of(post));
            given(problemRepository.findByNumber(2000L)).willReturn(Optional.of(problem));
            given(categoryRepository.findByNameIn(List.of("dp"))).willReturn(List.of(category));

            // when
            postService.editPost(userId, postId, requestDto);

            // then
            assertThat(post.getProblemNumber()).isEqualTo(2000L);
            assertThat(post.getTitle()).isEqualTo("수정된 제목");
            assertThat(post.getContent()).isEqualTo("수정된 내용");
            assertThat(post.getPostCategories().size()).isEqualTo(1);
            assertThat(post.getPostCategories().get(0).getCategory().getName()).isEqualTo("dp");
            assertThat(post.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("게시글이 존재하지 않으면 예외 발생")
        void editPost_게시글없음() {
            // given
            given(postRepository.findByIdWithUserAndCategories(postId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postService.editPost(userId, postId, PostCreateEditRequestDto.builder().build()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("해당 게시글이 존재하지 않습니다.");
        }

        @Test
        @DisplayName("본인이 아닌 사용자가 수정 시도 시 예외 발생")
        void editPost_권한없음() {
            // given
            User nonUser = User.builder().id(2L).build();
            post.setUser(nonUser);

            given(postRepository.findByIdWithUserAndCategories(postId)).willReturn(Optional.of(post));

            // when & then
            assertThatThrownBy(() -> postService.editPost(userId, postId, PostCreateEditRequestDto.builder().build()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("게시글 수정 권한이 없습니다.");
        }

        @Test
        @DisplayName("존재하지 않는 문제 번호 수정 시 예외 발생")
        void editPost_존재하지않는문제번호() {
            // given
            PostCreateEditRequestDto requestDto = PostCreateEditRequestDto.builder()
                .problemNumber(9999L)
                .build();

            given(postRepository.findByIdWithUserAndCategories(postId)).willReturn(Optional.of(post));
            given(problemRepository.findByNumber(9999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postService.editPost(userId, postId, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("해당 문제 번호를 가진 Problem이 없습니다");
        }

        @Test
        @DisplayName("존재하지 않는 카테고리 수정 시 예외 발생")
        void editPost_존재하지않는카테고리() {
            // given
            PostCreateEditRequestDto requestDto = PostCreateEditRequestDto.builder()
                .category(List.of("graph", "dp")) // 요청은 2개지만
                .build();

            given(postRepository.findByIdWithUserAndCategories(postId)).willReturn(Optional.of(post));
            given(categoryRepository.findByNameIn(List.of("graph", "dp")))
                .willReturn(List.of(category)); // 반환은 1개만

            // when & then
            assertThatThrownBy(() -> postService.editPost(userId, postId, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("존재하지 않는 카테고리가 포함되어 있습니다.");
        }

    }

    @Nested
    @DisplayName("게시글 삭제")
    class deletePostTest {

        @Test
        void deletePost_성공(){
            // given
            given(postRepository.findByIdWithUser(postId)).willReturn(Optional.of(post));

            // when
            postService.deletePost(userId, postId);

            // then
            assertThat(post.getDeletedAt()).isNotNull();
            verify(postRepository).findByIdWithUser(postId); // 메서드 호출 검증
        }

        @Test
        void deletePost_삭제권한없음(){
            User nonUser = User.builder()
                .id(999L)
                .nickname("권한없음")
                .build();

            post.setUser(nonUser);

            given(postRepository.findByIdWithUser(postId)).willReturn(Optional.of(post));

            assertThatThrownBy(() -> postService.deletePost(userId, postId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("게시글 삭제 권한이 없습니다.");
        }

        @Test
        void deletePost_존재하지않는게시글(){
            given(postRepository.findByIdWithUser(postId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postService.deletePost(userId, postId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("해당 게시글이 존재하지 않습니다.");
        }
    }
}