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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired private AuthTokenService authTokenService;
    @Autowired private ReportRepository reportRepository;
    @Autowired private CurationRepository curationRepository;

    private String accessToken;
    private Member reporter;

    @BeforeEach
    void setUp() {
        reporter = memberRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("BaseInitData: memberId=2 없음"));
        accessToken = authTokenService.genAccessToken(reporter);
    }

    @Test
    @DisplayName("신고한 큐레이션 목록을 조회할 수 있다")
    void getMyReportedCurations() throws Exception {
        // 🚨 테스트 전에 직접 신고 데이터 생성
        Curation targetCuration = curationRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("BaseInitData: curationId=1 없음"));

        reportRepository.save(
                Report.builder()
                        .reporter(reporter)
                        .curation(targetCuration)
                        .reportType(ReportType.ABUSE)
                        .build()
        );

        // 🔍 신고 내역 조회
        mockMvc.perform(get("/api/v1/reports/myreported/{memberId}", reporter.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("글이 성공적을 조회되었습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].curationId").value(targetCuration.getId()))
                .andExpect(jsonPath("$.data[0].reportType").value("ABUSE"));

        assertThat(reportRepository.findAllByReporter(reporter)).isNotEmpty();
    }
}