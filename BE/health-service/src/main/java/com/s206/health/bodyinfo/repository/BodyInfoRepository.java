package com.s206.health.bodyinfo.repository;

import com.s206.health.bodyinfo.dto.response.BodyInfoProjection;
import com.s206.health.bodyinfo.entity.BodyInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BodyInfoRepository extends JpaRepository<BodyInfo, Integer> {

	List<BodyInfo> findByUserId(Integer userId);

	Optional<BodyInfo> findByBodyIdAndIsDeletedFalse(Integer bodyId);

	@Query("SELECT b FROM BodyInfo b WHERE b.userId = :userId AND b.measurementDate = :date AND b.isDeleted = false ORDER BY b.createdAt DESC")
	Optional<BodyInfo> findLatestBodyInfoByMeasurementDate(@Param("userId") Integer userId,
														   @Param("date") LocalDate date);


	@Query("SELECT b.measurementDate AS measurementDate, " +
			"b.weight AS weight, " +
			"b.muscleMass AS muscleMass, " +
			"b.bodyFat AS bodyFat, " +
			"b.height AS height " +
			"FROM BodyInfo b " +
			"WHERE b.userId = :userId " +
			"AND b.isDeleted = false " +
			"AND b.measurementDate BETWEEN :startDate AND :endDate " +
			"AND b.createdAt = (" +
			"    SELECT MAX(b2.createdAt) " +
			"    FROM BodyInfo b2 " +
			"    WHERE b2.userId = b.userId " +
			"    AND b2.measurementDate = b.measurementDate " +
			"    AND b2.isDeleted = false" +
			")")
	List<BodyInfoProjection> findByUserIdAndMeasurementDateBetween(
			@Param("userId") Integer userId,
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate
	);

	@Query("SELECT DISTINCT b.userId FROM BodyInfo b WHERE b.userId IN (:userIds) AND b.measurementDate = :date AND b.isDeleted = false")
	List<Integer> findUserIdsWithWeightUpdate(@Param("userIds") List<Integer> userIds,
			@Param("date") LocalDate date);

	List<BodyInfo> findByUserIdAndIsDeletedFalse(Integer userId);
}
