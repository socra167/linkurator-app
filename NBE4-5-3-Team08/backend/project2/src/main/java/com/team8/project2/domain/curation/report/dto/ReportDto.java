package com.team8.project2.domain.curation.report.dto;

import com.team8.project2.domain.curation.curation.entity.Curation;
import com.team8.project2.domain.curation.report.entity.Report;
import com.team8.project2.domain.curation.report.entity.ReportType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {

    private Long reportId; // 신고 ID
    private Long curationId; // 큐레이션 ID
    private String curationTitle; // 큐레이션 제목
    private ReportType reportType; // 신고 유형
    private LocalDateTime reportDate; // 신고 날짜

    // Report 엔티티를 기반으로 ReportDto 객체를 생성하는 메서드
    public static ReportDto from(Report report) {
        Curation curation = report.getCuration();
        return ReportDto.builder()
                .reportId(report.getId())
                .curationId(curation.getId())
                .curationTitle(curation.getTitle()) // 큐레이션 제목을 가져옵니다
                .reportType(report.getReportType())
                .reportDate(report.getReportDate())
                .build();
    }
}