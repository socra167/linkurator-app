package com.team8.project2.domain.curation.report.dto;

import com.team8.project2.domain.curation.curation.entity.Curation;
import com.team8.project2.domain.curation.report.entity.Report;
import com.team8.project2.domain.curation.report.entity.ReportType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ReportedCurationsDetailResDto {
    private Long curationId;
    private String curationTitle;
    private String authorName;
    private List<ReportCountResDto> reportTypeCounts;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReportCountResDto {
        private ReportType reportType;
        private Long count;
    }

    public ReportedCurationsDetailResDto(Curation curation, List<Report> reports) {
        this.curationId = curation.getId();
        this.curationTitle = curation.getTitle();
        this.authorName = curation.getMemberName();

        // 🚀 신고 유형별 개수 계산 (한 곳에서 처리)
        this.reportTypeCounts = reports.stream()
                .collect(Collectors.groupingBy(Report::getReportType, Collectors.counting())) // 유형별 개수 집계
                .entrySet().stream()
                .map(entry -> new ReportCountResDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
