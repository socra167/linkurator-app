package com.team8.project2.domain.curation.report.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.team8.project2.domain.curation.curation.entity.Curation;
import com.team8.project2.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "Report")
public class Report {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reportId")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "curationId")
	private Curation curation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporterId")
	private Member reporter;

	@Enumerated(EnumType.STRING)
	private ReportType reportType;

	@CreatedDate
	private LocalDateTime reportDate;
}
