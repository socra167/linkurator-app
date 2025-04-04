package com.team8.project2.domain.curation.report.controller;

import com.team8.project2.domain.admin.dto.StatsResDto;
import com.team8.project2.domain.admin.service.AdminService;
import com.team8.project2.domain.comment.service.CommentService;
import com.team8.project2.domain.curation.curation.service.CurationService;
import com.team8.project2.domain.curation.report.dto.ReportDto;
import com.team8.project2.domain.curation.report.dto.ReportedCurationsDetailResDto;
import com.team8.project2.domain.curation.report.entity.Report;
import com.team8.project2.domain.curation.report.service.ReportService;
import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.service.MemberService;
import com.team8.project2.global.Rq;
import com.team8.project2.global.dto.RsData;
import com.team8.project2.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ApiV1ReportController {

    private final CurationService curationService;
    private final AdminService adminService;
    private final Rq rq;
    private final MemberService memberService;
    private final CommentService commentService;
    private final ReportService reportService;


    // ✅ 신고 조회
    @GetMapping("/myreported/{memberId}")
    public RsData<List<ReportDto>> getReports(@PathVariable Long memberId) {
        Member member = rq.getActor();
        if (member.getId() != memberId) {
            throw new ServiceException("404-1", "회원 정보가 일치하지 않습니다.");
        }

        Member Reporter = memberService.findById(memberId)
                .orElseThrow(() -> new ServiceException("404-1", "해당 회원을 찾을 수 없습니다."));

        List<ReportDto> reports = reportService.findAllByReporter(Reporter);
        return new RsData<>("200-1", "글이 성공적을 조회되었습니다.", reports);
    }
}
