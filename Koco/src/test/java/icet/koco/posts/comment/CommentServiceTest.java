package icet.koco.posts.comment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import icet.koco.alarm.dto.AlarmRequestDto;
import icet.koco.alarm.service.AlarmService;
import icet.koco.enums.ErrorMessage;
import icet.koco.fixture.CommentFixture;
import icet.koco.fixture.PostFixture;
import icet.koco.fixture.UserFixture;
import icet.koco.global.exception.ResourceNotFoundException;
import icet.koco.posts.dto.comment.CommentCreateEditRequestDto;
import icet.koco.posts.dto.comment.CommentCreateEditResponseDto;
import icet.koco.posts.entity.Comment;
import icet.koco.posts.entity.Post;
import icet.koco.posts.repository.CommentRepository;
import icet.koco.posts.repository.PostRepository;
import icet.koco.posts.service.CommentService;
import icet.koco.user.entity.User;
import icet.koco.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommentServiceTest {

	// Mock 객체 선언
	@InjectMocks
	private CommentService commentService;

	@Mock
	private AlarmService alarmService;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private PostRepository postRepository;

	@Mock
	private UserRepository userRepository;

	// 상수
	private static final Long VALID_USER_ID = 1L;
	private static final Long INVALID_USER_ID = 999L;
	private static final Long VALID_POST_ID = 100L;
	private static final Long INVALID_POST_ID = 999L;
	private static final Long VALID_PROBLEM_NUMBER = 30000L;
	private static final String VALID_COMMENT_TEXT = "This is a comment";
	private static final String EDIT_COMMENT_TEXT = "This is a comment";
	private static final Long VALID_COMMENT_ID = 1L;

	// 객체
	public User user;
	public Post post;
	public Comment savedComment;
	public Post postByAnotherUser;

	@BeforeEach
	void setUp() {
		user = UserFixture.validUser(VALID_USER_ID);
		post = PostFixture.post(VALID_POST_ID, user, VALID_PROBLEM_NUMBER);
		postByAnotherUser = PostFixture.post(VALID_POST_ID, UserFixture.anotherUser(), VALID_PROBLEM_NUMBER);
		savedComment = CommentFixture.comment(VALID_COMMENT_ID, user, post, VALID_COMMENT_TEXT);
	}

	@Nested
	@DisplayName("댓글 생성")
	class createCommentTest {

		@Test
		@DisplayName("성공 - 다른 사람의 글에 댓글 작성 시 알림 생성")
		void createComment_성공_알림생성() {
			// given
			given(userRepository.findByIdAndDeletedAtIsNull(VALID_USER_ID)).willReturn(Optional.of(user));
			given(postRepository.findByIdAndDeletedAtIsNull(VALID_POST_ID)).willReturn(Optional.of(postByAnotherUser));
			CommentCreateEditRequestDto requestDto = CommentFixture.requestDto(VALID_COMMENT_TEXT);
			given(commentRepository.save(any(Comment.class))).willReturn(savedComment);

			// when
			CommentCreateEditResponseDto responseDto = commentService.createComment(user.getId(), postByAnotherUser.getId(), requestDto);
			responseDto.setCommentId(VALID_COMMENT_ID);

			// then
			assertThat(responseDto).isNotNull();
			assertThat(responseDto.getCommentId()).isEqualTo(VALID_COMMENT_ID);

			verify(commentRepository).save(any(Comment.class));
			verify(postRepository).incrementCommentCount(VALID_POST_ID);
			verify(alarmService).createAlarmInternal(any(AlarmRequestDto.class));
		}

		@Test
		@DisplayName("성공 - 자기 자신의 글에 댓글 작성 시 알림 생성 안 함")
		void createComment_성공_알림미생성() {
			// given
			given(userRepository.findByIdAndDeletedAtIsNull(VALID_USER_ID)).willReturn(Optional.of(user));
			given(postRepository.findByIdAndDeletedAtIsNull(VALID_POST_ID)).willReturn(Optional.of(post));
			CommentCreateEditRequestDto requestDto = CommentFixture.requestDto(VALID_COMMENT_TEXT);
			given(commentRepository.save(any(Comment.class))).willReturn(savedComment);

			// when
			CommentCreateEditResponseDto responseDto = commentService.createComment(user.getId(), post.getId(), requestDto);
			responseDto.setCommentId(VALID_COMMENT_ID);

			// then
			assertThat(responseDto).isNotNull();
			assertThat(responseDto.getCommentId()).isEqualTo(VALID_COMMENT_ID);
			verify(commentRepository).save(any(Comment.class));
			verify(postRepository).incrementCommentCount(VALID_POST_ID);
			verify(alarmService, never()).createAlarmInternal(any(AlarmRequestDto.class));
		}

		@ParameterizedTest(name = "[{index}] {4}")
		@MethodSource("invalidCreateCommentArgs")
		void createComment_예외(
			Long userId,
			Long postId,
			Class <? extends RuntimeException> expectedException,
			ErrorMessage errorMessage,
			String caseDescription
		) {
			CommentCreateEditRequestDto requestDto = CommentFixture.requestDto(VALID_COMMENT_TEXT);

			// 존재하지 않는 사용자 ID
			given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(
				userId.equals(VALID_USER_ID)?Optional.of(user): Optional.empty()
			);

			// 존재하지 않는 postId
			given(postRepository.findByIdAndDeletedAtIsNull(postId)).willReturn(
				postId.equals(VALID_POST_ID)?Optional.of(post): Optional.empty()
			);

			assertThatThrownBy(() -> commentService.createComment(userId, postId, requestDto))
				.isInstanceOf(expectedException)
				.hasMessage(errorMessage.getMessage());
		}

		static Stream<Arguments> invalidCreateCommentArgs() {
			return Stream.of(
				// 존재하지 않는 사용자
				Arguments.of(INVALID_USER_ID, VALID_POST_ID, ResourceNotFoundException.class,
					ErrorMessage.USER_NOT_FOUND, "존재하지 않는 사용자"),

				// 존재하지 않는 게시물
				Arguments.of(VALID_USER_ID, INVALID_POST_ID, ResourceNotFoundException.class,
					ErrorMessage.POST_NOT_FOUND, "존재하지 않는 게시물")

			);
		}
	}

	@Nested
	@DisplayName("댓글 수정")

	class editCommentTest {
		@Test
		void editComment_성공() {
			// given
			given(userRepository.findByIdAndDeletedAtIsNull(VALID_USER_ID)).willReturn(Optional.of(user));
			given(postRepository.findByIdAndDeletedAtIsNull(VALID_POST_ID)).willReturn(Optional.of(post));
			CommentCreateEditRequestDto requestDto = CommentFixture.requestDto(EDIT_COMMENT_TEXT);

			// when


			// then
		}
	}
}
