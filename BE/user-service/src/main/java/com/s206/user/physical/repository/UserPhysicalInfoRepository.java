package com.s206.user.physical.repository;

import com.s206.user.physical.entity.UserPhysicalInfo;
import com.s206.user.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserPhysicalInfoRepository extends JpaRepository<UserPhysicalInfo, Long> {

    Optional<UserPhysicalInfo> findByUserAndDate(User user, LocalDate date);

    // 해당 날짜 이전 중 가장 최신 기록
    Optional<UserPhysicalInfo> findTopByUserAndDateLessThanEqualOrderByDateDesc(User user, LocalDate date);

    // 전체 기록 (최신순)
    List<UserPhysicalInfo> findAllByUserOrderByDateDesc(User user);
}
