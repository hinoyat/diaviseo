package com.s206.user.user.repository;

import com.s206.user.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findByEmailAndProvider(String email, String provider);

    Optional<User> findUserByUserId(Integer userId);
}
