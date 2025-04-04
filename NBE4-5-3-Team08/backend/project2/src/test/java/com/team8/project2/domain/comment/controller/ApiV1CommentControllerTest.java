package com.team8.project2.domain.comment.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team8.project2.domain.comment.dto.CommentDto;
import com.team8.project2.domain.comment.repository.CommentRepository;
import com.team8.project2.domain.comment.service.CommentService;
import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.repository.MemberRepository;
import com.team8.project2.domain.member.service.AuthTokenService;
import com.team8.project2.domain.member.service.MemberService;

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

		mockMvc.perform(post("/api/v1/curations/1/comments").header("Authorization", "Bearer " + authorAccessKey)
				.contentType("application/json")
				.content(new ObjectMapper().writeValueAsString(commentDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-2"))
			.andExpect(jsonPath("$.msg").value("댓글이 작성되었습니다."))
			.andExpect(jsonPath("$.data.id").isNumber())
			.andExpect(jsonPath("$.data.authorName").value("username"))
			.andExpect(jsonPath("$.data.content").value("content example"));

		// BaseInitData에서 추가된 샘플 데이터를 포함해 2개
		assertThat(commentService.getCommentsByCurationId(1L)).hasSize(2);
	}

	@Test
	@DisplayName("실패 - 인증 정보가 없으면 댓글 작성에 실패한다")
	void createCommentWithNoAuth() throws Exception {
		CommentDto commentDto = CommentDto.builder().content("content example").build();

		mockMvc.perform(post("/api/v1/curations/1/comments").contentType("application/json")
			.content(new ObjectMapper().writeValueAsString(commentDto))).andExpect(status().isUnauthorized());

		// 샘플 데이터를 제외하고 댓글이 추가되지 않음
		assertThat(commentRepository.count()).isEqualTo(1);
	}

	@Test
	@DisplayName("실패 - 인증 정보가 잘못되었으면 댓글 작성에 실패한다")
	void createCommentWithWrongAuth() throws Exception {
		String wrongAuth = "wrongAuth";

		CommentDto commentDto = CommentDto.builder().content("content example").build();

		mockMvc.perform(post("/api/v1/curations/1/comments").header("Authorization", "Bearer " + wrongAuth)
			.contentType("application/json")
			.content(new ObjectMapper().writeValueAsString(commentDto))).andExpect(status().isUnauthorized());

		// 샘플 데이터를 제외하고 댓글이 추가되지 않음
		assertThat(commentRepository.count()).isEqualTo(1);
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
			.andExpect(jsonPath("$.data[0].authorName").value("username"))
			.andExpect(jsonPath("$.data[0].content").value("comment test content"));
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