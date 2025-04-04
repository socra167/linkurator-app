package com.team8.project2.domain.comment.entity;

import java.time.LocalDateTime;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.team8.project2.domain.curation.curation.entity.Curation;
import com.team8.project2.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ReplyComment {

	/**
	 * 댓글의 고유 ID (자동 생성)
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 댓글 작성자의 사용자 ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "memberId", nullable = false)
	private Member author;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "commentId", nullable = false)
	private Comment comment;

	/**
	 * 댓글이 속한 큐레이션
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "curation_id", nullable = false)
	private Curation curation;

	/**
	 * 댓글 내용 (텍스트 형태, 필수값)
	 */
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	/**
	 * 댓글 생성 시간 (수정 불가)
	 */
	@CreatedDate
	@Column(nullable = false, updatable = false)
	@Setter(AccessLevel.PRIVATE)
	private LocalDateTime createdAt;

	/**
	 * 댓글 수정 시간
	 */
	@LastModifiedDate
	@Column(nullable = false)
	@Setter(AccessLevel.PRIVATE)
	private LocalDateTime modifiedAt;

	public String getAuthorName() {
		return author.getUsername();
	}

	public String getAuthorProfileImageUrl() {
		return author.getProfileImage();
	}

	public void updateContent(String content) {
		this.content = content;
	}

	public Long getAuthorId() {
		return this.author.getId();
	}
}
