package com.team8.project2.domain.report.service;

import com.team8.project2.domain.curation.curation.entity.Curation;
import com.team8.project2.domain.curation.curation.repository.CurationRepository;
import com.team8.project2.domain.curation.report.dto.ReportedCurationsDetailResDto;
import com.team8.project2.domain.curation.report.entity.Report;
import com.team8.project2.domain.curation.report.entity.ReportType;
import com.team8.project2.domain.curation.report.repository.ReportRepository;
import com.team8.project2.domain.curation.report.service.ReportService;
import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReportServiceTest {

    @Autowired
    private ReportService reportService;
    @Autowired private ReportRepository reportRepository;
    @Autowired private CurationRepository curationRepository;
    @Autowired private MemberRepository memberRepository;

    @Test
    @DisplayName("신고가 여러 건 존재할 때 유형별 개수를 올바르게 집계한다")
    void testGetReportedCurationsDetail() {
        // given
        Curation curation = curationRepository.findById(1L).orElseThrow();
        Member reporter1 = memberRepository.findById(2L).orElseThrow();
        Member reporter2 = memberRepository.findById(3L).orElseThrow();

        reportRepository.save(Report.builder()
                .curation(curation)
                .reporter(reporter1)
                .reportType(ReportType.ABUSE)
                .build());

        reportRepository.save(Report.builder()
                .curation(curation)
                .reporter(reporter2)
                .reportType(ReportType.SPAM)
                .build());

        reportRepository.save(Report.builder()
                .curation(curation)
                .reporter(reporter1)
                .reportType(ReportType.ABUSE)
                .build());

        // when
        List<ReportedCurationsDetailResDto> result = reportService.getReportedCurationsDetailResDtos(List.of(curation.getId()));

        // then
        assertThat(result).hasSize(1);
        ReportedCurationsDetailResDto dto = result.get(0);
        assertThat(dto.getReportTypeCounts()).hasSize(2); // ABUSE, SPAM
        assertThat(dto.getReportTypeCounts()).anySatisfy(type ->
                assertThat(type.getReportType()).isIn(ReportType.ABUSE, ReportType.SPAM));
    }
}

