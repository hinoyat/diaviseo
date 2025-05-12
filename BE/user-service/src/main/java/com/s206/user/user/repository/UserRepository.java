package com.s206.user.user.repository;

import com.s206.user.user.dto.response.UserResponse;
import com.s206.user.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);

	Optional<User> findByEmailAndProvider(String email, String provider);

	Optional<User> findUserByUserId(Integer userId);

	@Query("SELECT u.userId as userId, u.nickname as nickname, u.updatedAt as updatedAt, u.name as name FROM User u WHERE u.isDeleted = false AND u.notificationEnabled = true")
	List<UserResponse> findNotificationEnabled();

	@Query("SELECT u.fcmToken FROM User u WHERE u.userId = :userId")
	Optional<String> findFcmTokenByUserId(Integer userId);

	@Modifying
	@Query("UPDATE User u SET u.fcmToken = :token WHERE u.userId = :userId")
	void updateFcmToken(@Param("userId") Integer userId, @Param("token") String token);
}
