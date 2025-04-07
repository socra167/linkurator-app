package com.team8.project2.domain.comment.repository

import com.team8.project2.domain.comment.entity.ReplyComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReplyCommentRepository : JpaRepository<ReplyComment, Long?>
