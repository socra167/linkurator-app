package com.team8.project2.domain.comment.repository;

import com.team8.project2.domain.comment.entity.Comment;
import com.team8.project2.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 댓글(Comment) 엔티티에 대한 데이터 접근 레이어(Repository)입니다.
 * JPA를 사용하여 데이터베이스 연산을 수행합니다.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 특정 큐레이션 ID에 해당하는 모든 댓글을 조회합니다.
     * @param curationId 큐레이션 ID
     * @return 해당 큐레이션에 속한 댓글 리스트
     */
    List<Comment> findByCurationId(Long curationId);

    List<Comment> findAllByAuthor_Id(Long memberId);

    List<Comment> findAllByAuthor(Member author);

    void deleteByAuthor(Member member);
}
