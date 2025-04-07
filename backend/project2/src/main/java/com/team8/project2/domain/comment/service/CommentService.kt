package com.team8.project2.domain.comment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.annotations.Comments;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team8.project2.domain.comment.dto.CommentDto;
import com.team8.project2.domain.comment.dto.ReplyCommentDto;
import com.team8.project2.domain.comment.entity.Comment;
import com.team8.project2.domain.comment.entity.ReplyComment;
import com.team8.project2.domain.comment.repository.CommentRepository;
import com.team8.project2.domain.comment.repository.ReplyCommentRepository;
import com.team8.project2.domain.curation.curation.entity.Curation;
import com.team8.project2.domain.curation.curation.repository.CurationRepository;
import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.global.Rq;
import com.team8.project2.global.exception.ServiceException;

import lombok.RequiredArgsConstructor;

/**
 * 댓글(Comment) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 댓글 작성, 조회, 삭제 기능을 제공합니다.
 * 특정 큐레이션(Curation)과 연관된 댓글을 관리합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

	private final CommentRepository commentRepository;
	private final CurationRepository curationRepository;
	private final ReplyCommentRepository replyCommentRepository;
	private final Rq rq;

	/**
	 * 새로운 댓글을 생성합니다.
	 * @param commentDto 댓글 생성 요청 데이터
	 * @return 생성된 댓글의 정보를 반환합니다.
	 * @throws ServiceException 큐레이션 ID가 제공되지 않거나 존재하지 않을 경우 예외 발생
	 */
	@Transactional
	public CommentDto createComment(Member author, Long curationId, CommentDto commentDto) {
		// curationId를 이용해 Curation 조회
		Curation curation = curationRepository.findById(curationId)
			.orElseThrow(
				() -> new ServiceException("404-1", "해당 큐레이션을 찾을 수 없습니다. (id: " + curationId + ")"));

		// Curation 객체를 사용해 Comment 생성
		Comment comment = commentDto.toEntity(author, curation);
		Comment savedComment = commentRepository.save(comment);
		return CommentDto.fromEntity(savedComment);
	}

	/**
	 * 특정 큐레이션 ID에 해당하는 모든 댓글을 조회합니다.
	 * @param curationId 큐레이션 ID
	 * @return 해당 큐레이션에 등록된 댓글 목록을 반환합니다.
	 */
	@Transactional(readOnly = true)
	public List<CommentDto> getCommentsByCurationId(Long curationId) {
		List<Comment> comments = commentRepository.findByCurationId(curationId);
		return comments.stream().map(CommentDto::fromEntity).collect(Collectors.toList());
	}

	/**
	 * 특정 댓글을 삭제합니다.
	 * @param commentId 삭제할 댓글 ID
	 * @throws ServiceException 해당 댓글이 존재하지 않을 경우 예외 발생
	 */
	@Transactional
	public void deleteComment(Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new ServiceException("404-2", "해당 댓글을 찾을 수 없습니다."));
		commentRepository.delete(comment);
	}

	@Transactional
	public CommentDto updateComment(Long commentId, CommentDto commentDto) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new ServiceException("404-2", "해당 댓글을 찾을 수 없습니다."));
		comment.updateContent(commentDto.getContent());
		return CommentDto.fromEntity(comment);
	}

	// 댓글 수정 권한 체크 메서드
	public boolean canEditComment(Long commentId, UserDetails userDetails) {
		if (userDetails == null) {
			throw new ServiceException("401-1", "사용자 정보가 없습니다.");
		}

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new ServiceException("404-2", "해당 댓글을 찾을 수 없습니다."));

		if (!comment.getAuthor().getMemberId().equals(userDetails.getUsername())) {
			throw new ServiceException("403-2", "댓글을 수정할 권한이 없습니다.");
		}

		return true;
	}

	// 댓글 삭제 권한 체크 메서드
	public boolean canDeleteComment(Long commentId, UserDetails userDetails) {
		if (userDetails == null) {
			throw new ServiceException("401-1", "사용자 정보가 없습니다.");
		}

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new ServiceException("404-2", "해당 댓글을 찾을 수 없습니다."));

		if (!comment.getAuthor().getMemberId().equals(userDetails.getUsername())) {
			throw new ServiceException("403-2", "댓글을 삭제할 권한이 없습니다.");
		}

		return true;
	}

	@Transactional
	public List<CommentDto> findAllByAuthorId(Long authorId) {
		return commentRepository.findAllByAuthor_Id(authorId).stream()
				.map(CommentDto::fromEntity) // 정적 메서드 활용
				.collect(Collectors.toList());
	}

	@Transactional
	public List<Comment> findAllByAuthor(Member author) {
		return commentRepository.findAllByAuthor(author);
	}


	/**
	 * 댓글에 대한 답글 작성
	 * @param curationId 큐레이션 ID
	 * @param commentId 답글을 작성할 댓글 ID
	 * @param content 답글의 내용
	 * @return
	 */
	@Transactional
	public ReplyCommentDto createReplyComment(Long curationId, Long commentId, String content) {
		Curation curation = curationRepository.findById(curationId)
			.orElseThrow(() -> new ServiceException("404-1", "큐레이션이 존재하지 않습니다."));
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new ServiceException("404-2", "댓글이 존재하지 않습니다."));
		ReplyComment reply = ReplyComment.builder()
			.curation(curation)
			.comment(comment)
			.author(rq.getActor())
			.content(content)
			.build();
		ReplyComment savedReply = replyCommentRepository.save(reply);
		return ReplyCommentDto.fromEntity(savedReply);
	}

	public boolean canEditReply(Long replyId, UserDetails userDetails) {
		if (userDetails == null) {
			throw new ServiceException("401-1", "사용자 정보가 없습니다.");
		}

		ReplyComment reply = replyCommentRepository.findById(replyId)
			.orElseThrow(() -> new ServiceException("404-2", "해당 답글을 찾을 수 없습니다."));

		if (!reply.getAuthor().getMemberId().equals(userDetails.getUsername())) {
			throw new ServiceException("403-2", "답글을 수정할 권한이 없습니다.");
		}

		return true;
	}

	public boolean canDeleteReply(Long replyId, UserDetails userDetails) {
		if (userDetails == null) {
			throw new ServiceException("401-1", "사용자 정보가 없습니다.");
		}

		ReplyComment reply = replyCommentRepository.findById(replyId)
			.orElseThrow(() -> new ServiceException("404-2", "해당 답글을 찾을 수 없습니다."));

		if (!reply.getAuthor().getMemberId().equals(userDetails.getUsername())) {
			throw new ServiceException("403-2", "답글을 삭제할 권한이 없습니다.");
		}

		return true;
	}

	@Transactional
	public ReplyCommentDto updateReply(Long replyId, CommentDto replyDto) {
		ReplyComment reply = replyCommentRepository.findById(replyId)
			.orElseThrow(() -> new ServiceException("404-2", "해당 댓글을 찾을 수 없습니다."));
		reply.updateContent(replyDto.getContent());
		return ReplyCommentDto.fromEntity(reply);
	}

	@Transactional
	public void deleteReply(Long replyId) {
		ReplyComment replyComment = replyCommentRepository.findById(replyId)
			.orElseThrow(() -> new ServiceException("404-2", "해당 답글을 찾을 수 없습니다."));
		replyCommentRepository.delete(replyComment);
	}

}

