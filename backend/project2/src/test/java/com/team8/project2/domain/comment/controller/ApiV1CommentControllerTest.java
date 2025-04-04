package com.team8.project2.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team8.project2.domain.comment.dto.CommentDto;
import com.team8.project2.domain.comment.repository.CommentRepository;
import com.team8.project2.domain.comment.service.CommentService;
import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.repository.MemberRepository;
import com.team8.project2.domain.member.service.AuthTokenService;
import com.team8.project2.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ApiV1CommentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CommentService commentService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private AuthTokenService authTokenService;

	String authorAccessKey;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private MemberService memberService;

	@BeforeEach
	void setUp() {
		Member author = memberRepository.findById(1L).get();
		authorAccessKey = authTokenService.genAccessToken(author);
	}

	@Test
	@DisplayName("댓글을 작성할 수 있다")
	void createComment() throws Exception {
		CommentDto commentDto = CommentDto.builder().content("content example").build();

		mockMvc.perform(post("/api/v1/curations/1/comments")
						.header("Authorization", "Bearer " + authorAccessKey)
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(commentDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("200-2"))
				.andExpect(jsonPath("$.msg").value("댓글이 작성되었습니다."))
				.andExpect(jsonPath("$.data.authorName").value("username"))
				.andExpect(jsonPath("$.data.content").value("content example"));

		List<CommentDto> comments = commentService.getCommentsByCurationId(1L);
		assertThat(comments).anyMatch(c -> c.getContent().equals("content example"));
	}

	@Test
	@DisplayName("실패 - 인증 정보가 없으면 댓글 작성에 실패한다")
	void createCommentWithNoAuth() throws Exception {
		CommentDto commentDto = CommentDto.builder().content("content example").build();

		mockMvc.perform(post("/api/v1/curations/1/comments")
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(commentDto)))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("401-1"))
				.andExpect(jsonPath("$.msg").value("접근이 거부되었습니다. 로그인 상태를 확인해 주세요.")); // 실제 응답 메시지 확인 필요

		// 댓글 수 변화 확인 (DB 초기화 되어있다면 0일 것)
		assertThat(commentService.getCommentsByCurationId(600L)).isEmpty();
	}

	@Test
	@DisplayName("댓글을 조회할 수 있다")
	void getCommentsByCurationId() throws Exception {
		Member author = memberRepository.findById(1L).get();
		createCommentAtCuration(1L, author);

		mockMvc.perform(get("/api/v1/curations/1/comments"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-2"))
			.andExpect(jsonPath("$.msg").value("댓글이 조회되었습니다."))
			.andExpect(jsonPath("$.data[0].id").value("1"))
			.andExpect(jsonPath("$.data[0].authorName").value("other"))
			.andExpect(jsonPath("$.data[0].content").value("정말 유용한 정보네요! 감사합니다."));
	}

	private CommentDto createCommentAtCuration(Long curationId, Member author) {
		CommentDto commentDto = CommentDto.builder().content("content example").build();
		return commentService.createComment(author, curationId, commentDto);
	}

	@Test
	@DisplayName("댓글 작성자는 댓글을 삭제할 수 있다")
	void deleteComment() throws Exception {
		Member author = memberRepository.findById(1L).get();
		CommentDto savedCommentDto = createCommentAtCuration(1L, author);

		mockMvc.perform(
				delete("/api/v1/curations/1/comments/%d".formatted(savedCommentDto.getId())).header("Authorization",
					"Bearer " + authorAccessKey))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-1"))
			.andExpect(jsonPath("$.msg").value("댓글이 삭제되었습니다."));
	}

	@Test
	@DisplayName("실패 - 다른 사람의 댓글을 삭제할 수 없다")
	void deleteOthersComment() throws Exception {
		Member otherAuthor = memberRepository.findById(2L).get();
		CommentDto savedCommentDto = createCommentAtCuration(1L, otherAuthor);

		mockMvc.perform(
				delete("/api/v1/curations/1/comments/%d".formatted(savedCommentDto.getId())).header("Authorization",
					"Bearer " + authorAccessKey))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("403-2"))
			.andExpect(jsonPath("$.msg").value("댓글을 삭제할 권한이 없습니다."));
	}

	@Test
	@DisplayName("댓글 작성자는 댓글을 수정할 수 있다")
	void updateComment() throws Exception {
		Member author = memberRepository.findById(1L).get();
		CommentDto savedCommentDto = createCommentAtCuration(1L, author);

		mockMvc.perform(
				put("/api/v1/curations/1/comments/%d".formatted(savedCommentDto.getId()))
					.content("""
						{
							"content" : "new content"
						}
						""".trim().stripIndent())
					.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
					.header("Authorization", "Bearer " + authorAccessKey))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-2"))
			.andExpect(jsonPath("$.msg").value("댓글이 수정되었습니다."));
	}

	@Test
	@DisplayName("실패 - 다른 사람의 댓글을 수정할 수 없다")
	void updateOthersComment() throws Exception {
		Member otherAuthor = memberRepository.findById(2L).get();
		CommentDto savedCommentDto = createCommentAtCuration(1L, otherAuthor);

		mockMvc.perform(
				put("/api/v1/curations/1/comments/%d".formatted(savedCommentDto.getId()))
					.content("""
						{
							"content" : "new content"
						}
						""".trim().stripIndent())
					.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
					.header("Authorization", "Bearer " + authorAccessKey))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("403-2"))
			.andExpect(jsonPath("$.msg").value("댓글을 수정할 권한이 없습니다."));
	}
}