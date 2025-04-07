package com.team8.project2.domain.curation.report.repository;

import com.team8.project2.domain.curation.curation.entity.Curation;
import com.team8.project2.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import com.team8.project2.domain.curation.report.entity.Report;
import com.team8.project2.domain.curation.report.entity.ReportType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
	boolean existsByCurationIdAndReporterIdAndReportType(Long curationId, Long id, ReportType reportType);

	@Query("SELECT r.curation.id, r.reportType, COUNT(r) " +
			"FROM Report r " +
			"WHERE r.curation.id IN :curationIds " +
			"GROUP BY r.curation.id, r.reportType")
	List<Object[]> countReportsByCurationIds(@Param("curationIds") List<Long> curationIds);

	List<Report> findByCurationIdIn(List<Long> reportedCurationIds);

	void deleteByCurationId(Long curationId);

	List<Report> findAllByReporter(Member reporter);
}
