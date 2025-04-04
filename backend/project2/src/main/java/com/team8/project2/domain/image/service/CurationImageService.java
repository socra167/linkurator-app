package com.team8.project2.domain.image.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.team8.project2.domain.image.entity.CurationImage;
import com.team8.project2.domain.image.repository.CurationImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurationImageService {

	private final S3Uploader s3Uploader;
	private final CurationImageRepository curationImageRepository;

	@Transactional
	public String uploadImage(MultipartFile file) throws IOException {
		String imageName;
		imageName = s3Uploader.uploadFile(file);

		CurationImage curationImage = CurationImage.builder()
			.imageName(imageName)
			.build();
		curationImageRepository.save(curationImage);

		return s3Uploader.getBaseUrl() + imageName;
	}

	@Transactional
	public List<CurationImage> findByCurationId(Long curationId) {
		return curationImageRepository.findByCurationId(curationId);
	}

	@Transactional
	public void deleteByImageName(String imageName) {
		curationImageRepository.deleteByImageName(imageName);
	}

	@Transactional
	public void deleteByCurationId(Long curationId) {
		curationImageRepository.deleteByCurationId(curationId);
	}
}
