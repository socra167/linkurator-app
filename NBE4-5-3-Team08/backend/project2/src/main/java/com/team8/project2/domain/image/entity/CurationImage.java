package com.team8.project2.domain.image.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "curationImages")
public class CurationImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "curationImageId", nullable = false)
	private Long id;

	@Column(name = "imageName", nullable = false, unique = true, updatable = false)
	private String imageName;

	// 이벤트 기반으로 처리하기 위해 외래키 미사용
	@Column(name = "curationId")
	private Long curationId;

	@CreatedDate
	@Setter(AccessLevel.PRIVATE)
	@Column(name = "uploadedAt", nullable = false, updatable = false)
	private LocalDateTime uploadedAt;

	public void setCurationIdIfNull(Long curationId) {
		if (this.curationId == null) {
			this.curationId = curationId;
		}
	}
}
