package com.team8.project2.domain.image.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team8.project2.domain.image.service.CurationImageService;
import com.team8.project2.global.exception.ServiceException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class CurationImageController {

	private final CurationImageService curationImageService;

	@PostMapping("/upload")
	public ResponseEntity<String> uploadCurationImage(@RequestParam("file") MultipartFile file) {
		String imageUrl;
		try {
			imageUrl = curationImageService.uploadImage(file);
		} catch (IOException e) {
			return ResponseEntity.status(500).body("파일 업로드 실패");
		}
		if (imageUrl == null) {
			throw new ServiceException("500-1", "이미지 업로드에 실패했습니다.");
		}

		return ResponseEntity.ok(imageUrl);
	}
}