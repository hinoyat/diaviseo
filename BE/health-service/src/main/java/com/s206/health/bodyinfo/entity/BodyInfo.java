package com.s206.health.bodyinfo.entity;

import com.s206.health.persistence.converter.DecimalEncryptConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "body_tb")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BodyInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bodyId;

	@Column(nullable = false)
	private Integer userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private InputType inputType;

	// 커스텀 컨버터를 지정하는 역할.
	@Convert(converter = DecimalEncryptConverter.class)
	@Column(nullable = false)
	private BigDecimal weight;

	@Convert(converter = DecimalEncryptConverter.class)
	@Column(nullable = true)
	private BigDecimal bodyFat;

	@Column(name = "skeletal_muscle", nullable = true)
	@Convert(converter = DecimalEncryptConverter.class)
	private BigDecimal muscleMass;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;
	// JPA에서 테이블 컬럼 생성 시 사용할 SQL 구문을 직접 지정
	@Column(nullable = false)
	private Boolean isDeleted = false;


	public BodyInfo updatePartial(BigDecimal weight, BigDecimal bodyFat, BigDecimal muscleMass) {
		if (weight != null) {
			this.weight = weight;
		}
		if (bodyFat != null) {
			this.bodyFat = bodyFat;
		}
		if (muscleMass != null) {
			this.muscleMass = muscleMass;
		}
		this.updatedAt = LocalDateTime.now();
		return this;
	}
}
