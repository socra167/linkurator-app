package com.team8.project2.domain.report.controller;

import com.team8.project2.domain.curation.curation.entity.Curation;
import com.team8.project2.domain.curation.curation.repository.CurationRepository;
import com.team8.project2.domain.curation.report.entity.Report;
import com.team8.project2.domain.curation.report.entity.ReportType;
import com.team8.project2.domain.curation.report.repository.ReportRepository;
import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.repository.MemberRepository;
import com.team8.project2.domain.member.service.AuthTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private AuthTokenService authTokenService;
    @Autowired private ReportRepository reportRepository;
    @Autowired private CurationRepository curationRepository;

    private String accessToken;
    private Member reporter;

    @BeforeEach
    void setUp() {
        reporter = memberRepository.findById(2L).orElseThrow();
        accessToken = authTokenService.genAccessToken(reporter);
    }

    @Test
    @DisplayName("신고한 큐레이션 목록을 조회할 수 있다")
    void getMyReportedCurations() throws Exception {
        Curation curation = curationRepository.findById(1L).orElseThrow();
        reportRepository.save(Report.builder().reporter(reporter).curation(curation).reportType(ReportType.ABUSE).build());

        mockMvc.perform(get("/api/v1/reports/myreported/{memberId}", reporter.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.data[0].curationId").value(curation.getId()))
                .andExpect(jsonPath("$.data[0].reportType").value("ABUSE"));
    }

    @Test
    @DisplayName("신고 내역이 없을 경우 빈 배열을 반환한다")
    void getEmptyReportedCurations() throws Exception {
        mockMvc.perform(get("/api/v1/reports/myreported/{memberId}", reporter.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("다른 사용자의 신고 내역은 조회할 수 없다")
    void cannotAccessOthersReportList() throws Exception {
        mockMvc.perform(get("/api/v1/reports/myreported/{memberId}", 3L)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value("404-1"));
    }
}