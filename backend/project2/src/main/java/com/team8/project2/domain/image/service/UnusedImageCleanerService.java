package com.team8.project2.domain.image.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team8.project2.domain.image.entity.CurationImage;
import com.team8.project2.domain.image.repository.CurationImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnusedImageCleanerService {

	private final CurationImageRepository curationImageRepository;
	private final S3Uploader s3Uploader;

	// 매일 오후 18시에 실행
	@Transactional
	@Scheduled(cron = "0 0 5 * * *")
	public void cleanUnusedImages() {
		// 현재 시간에서 하루를 빼 삭제 기준 시간 설정
		LocalDateTime cutoffDate = LocalDateTime.now().minus(1, ChronoUnit.DAYS);

		// 하루가 지난 사용되지 않은 이미지 S3, DB에서 삭제
		List<CurationImage> unusedImages = curationImageRepository.findUnusedImages(cutoffDate);
		for (CurationImage unusedImage : unusedImages) {
			s3Uploader.deleteFile(unusedImage.getImageName());
			curationImageRepository.delete(unusedImage);
		}
	}
}
