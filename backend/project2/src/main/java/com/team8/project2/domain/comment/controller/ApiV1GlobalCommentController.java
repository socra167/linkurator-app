package com.team8.project2.domain.comment.controller;

import com.team8.project2.domain.comment.dto.CommentDto;
import com.team8.project2.domain.comment.entity.Comment;
import com.team8.project2.domain.comment.service.CommentService;
import com.team8.project2.domain.curation.curation.entity.Curation;
import com.team8.project2.domain.curation.curation.service.CurationService;
import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.service.MemberService;
import com.team8.project2.global.Rq;
import com.team8.project2.global.dto.RsData;
import com.team8.project2.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class ApiV1GlobalCommentController {
    private final MemberService memberService;
    private final CommentService commentService;
    private final CurationService curationService;
    private final Rq rq;

    @GetMapping("/mycomments")
    public RsData<List<CommentDto>> getCommentsByCurationId() {
        Member author = rq.getActor();
        Member member = memberService.findById(author.getId())
                .orElseThrow(() -> new ServiceException("404-1", "해당 회원을 찾을 수 없습니다."));
        List<CommentDto> commentDtos = commentService.findAllByAuthorId(member.getId());

        return RsData.success("내 댓글 조회 성공", commentDtos);
    }

    @DeleteMapping("/{id}")
    public RsData<Void> deleteComment(
            @PathVariable(name = "id") Long commentId
    ) {
        Member author = rq.getActor();
        commentService.deleteComment(commentId);
        return new RsData<>("200-1", "댓글이 삭제되었습니다.");
    }
}
