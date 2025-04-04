package com.team8.project2.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team8.project2.domain.comment.entity.ReplyComment;

@Repository
public interface ReplyCommentRepository extends JpaRepository<ReplyComment, Long> {
}
