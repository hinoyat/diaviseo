package com.s206.health.bodyinfo.repository;

import com.s206.health.bodyinfo.entity.BodyInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyInfoRepository extends JpaRepository<BodyInfo, Integer> {

	List<BodyInfo> findByUserId(Integer userId);
}
