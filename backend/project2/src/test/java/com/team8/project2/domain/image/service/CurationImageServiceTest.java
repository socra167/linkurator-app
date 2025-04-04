package com.team8.project2.domain.image.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.team8.project2.domain.image.repository.CurationImageRepository;

@ExtendWith(MockitoExtension.class)
class CurationImageServiceTest {

	@InjectMocks
	private CurationImageService curationImageService;

	@Mock
	private CurationImageRepository curationImageRepository;

	@Mock
	private S3Uploader s3Uploader;

	@Test
	@DisplayName("큐레이션 작성 중 이미지를 업로드하면 이미지 정보가 저장되고 S3에 업로드된다")
	void uploadImage() throws IOException {
		MockMultipartFile mockFile = new MockMultipartFile(
			"file",
			"test-image.jpg",
			"image/jpeg",
			"test-image-content".getBytes()
		);
		when(s3Uploader.uploadFile(mockFile)).thenReturn("saved-image.webp");

		curationImageService.uploadImage(mockFile);

		verify(s3Uploader).uploadFile(mockFile);
		verify(curationImageRepository).save(any());
	}

	@Test
	@DisplayName("큐레이션 ID로 큐레이션에 첨부된 이미지를 찾을 수 있다")
	void findByCurationId() {
		var curationId = 1L;
		curationImageService.findByCurationId(curationId);
		verify(curationImageRepository).findByCurationId(curationId);
	}

	@Test
	@DisplayName("이미지명으로 큐레이션에 첨부된 이미지 정보를 삭제할 수 있다")
	void deleteByImageName() {
		var imageName = "test-image.jpg";
		curationImageService.deleteByImageName(imageName);
		verify(curationImageRepository).deleteByImageName(imageName);
	}

	@Test
	@DisplayName("큐레이션 ID로 큐레이션에 첨부된 모든 이미지 정보를 삭제할 수 있다")
	void deleteByCurationId() {
		var curationId = 1L;
		curationImageService.deleteByCurationId(curationId);
		verify(curationImageRepository).deleteByCurationId(curationId);
	}
}