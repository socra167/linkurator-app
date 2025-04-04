package com.team8.project2.domain.image.service;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team8.project2.domain.image.entity.CurationImage;
import com.team8.project2.domain.image.repository.CurationImageRepository;

@ExtendWith(MockitoExtension.class)
public class UnusedImageCleanerServiceTest {

	@InjectMocks
	private UnusedImageCleanerService unusedImageCleanerService;

	@Mock
	private CurationImageRepository curationImageRepository;

	@Mock
	private S3Uploader s3Uploader;

	@Test
	@DisplayName("이미지 삭제 스케줄러에서 삭제 작업이 수행된다")
	public void testCleanUnusedImages() {
		var imageName = "imageName.png";
		when(curationImageRepository.findUnusedImages(any(LocalDateTime.class)))
			.thenReturn(List.of(CurationImage.builder()
				.imageName(imageName)
				.build()));

		// when: cleanUnusedImages 메서드 호출
		unusedImageCleanerService.cleanUnusedImages();

		// then: deleteUnusedImage 메서드가 호출되었는지 확인
		verify(curationImageRepository, times(1)).delete(any(CurationImage.class));
		verify(s3Uploader, times(1)).deleteFile(imageName);
	}
}