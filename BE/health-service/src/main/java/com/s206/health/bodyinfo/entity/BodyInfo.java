package com.s206.health.bodyinfo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "body_tb")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class BodyInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bodyId;

	@Column(nullable = false)
	private Integer userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private InputType inputType;

	@Column
	private BigDecimal height;

	@Column(nullable = false)
	private BigDecimal weight;

	@Column(nullable = true)
	private BigDecimal bodyFat;

	@Column(name = "skeletal_muscle", nullable = true)
	private BigDecimal muscleMass;

	@Column(name = "measurement_date", nullable = false)
	private LocalDate measurementDate;

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


	public BodyInfo updatePartial(BigDecimal weight, BigDecimal bodyFat, BigDecimal muscleMass,
			BigDecimal height,
			LocalDate measurementDate) {
		if (weight != null) {
			this.weight = weight;
		}

		if (height != null) {
			this.height = height;
		}
		if (bodyFat != null) {
			this.bodyFat = bodyFat;
		}
		if (muscleMass != null) {
			this.muscleMass = muscleMass;
		}

		if (measurementDate != null) {
			this.measurementDate = measurementDate;
		}

		this.updatedAt = LocalDateTime.now();
		return this;
	}

	public void markAsDeleted() {
		this.updatedAt = LocalDateTime.now();
		this.deletedAt = LocalDateTime.now();
		this.isDeleted = true;
	}
}
