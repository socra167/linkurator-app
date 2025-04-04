package com.team8.project2.domain.image.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.team8.project2.domain.image.entity.CurationImage;

public interface CurationImageRepository extends JpaRepository<CurationImage, Long> {

	Optional<CurationImage> findByImageName(String imageUrl);

	List<CurationImage> findByCurationId(Long curationId);

	void deleteByImageName(String imageName);

	void deleteByCurationId(Long curationId);

	@Query("SELECT c FROM CurationImage c WHERE c.curationId IS NULL AND c.uploadedAt <= :cutoffDate")
	List<CurationImage> findUnusedImages(LocalDateTime cutoffDate);
}
