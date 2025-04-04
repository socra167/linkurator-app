package com.team8.project2.domain.image.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
class S3UploaderTest {

	@Mock
	private S3Client s3Client;

	@Mock
	private MultipartFile multipartFile;

	@InjectMocks
	private S3Uploader s3Uploader;

	@Test
	@DisplayName("S3 버킷에서 이미지를 삭제할 수 있다")
	void deleteFile() {
		var fileName = "test-image.jpg";
		s3Uploader.deleteFile(fileName);
		verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
	}

	@Test
	@DisplayName("S3 버킷에 이미지를 업로드할 수 있다")
	void uploadFile() throws IOException {
		var fileName = UUID.randomUUID() + "_test-image.jpg";
		when(multipartFile.getOriginalFilename()).thenReturn("test-image.jpg");
		when(multipartFile.getContentType()).thenReturn("image/jpeg");
		when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
		when(multipartFile.getSize()).thenReturn(10L);

		var uploadedFileName = s3Uploader.uploadFile(multipartFile);
		assertNotNull(uploadedFileName);
		verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
	}
}