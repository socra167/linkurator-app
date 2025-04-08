package com.team8.project2.domain.curation.report.entity

import com.team8.project2.domain.curation.curation.entity.Curation
import com.team8.project2.domain.member.entity.Member
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "Report")
@EntityListeners(AuditingEntityListener::class)
class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reportId")
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curationId")
    val curation: Curation,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporterId")
    val reporter: Member,
    @Enumerated(EnumType.STRING)
    val reportType: ReportType,
    @CreatedDate
    @Column(updatable = false)
    val reportDate: LocalDateTime? = null,
) {
    constructor(curation: Curation, reportType: ReportType, reporter: Member) : this(
        id = null,
        curation = curation,
        reporter = reporter,
        reportType = reportType,
    )
}
