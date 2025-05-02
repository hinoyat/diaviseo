package com.s206.health.nutrition.favorite.repository;

import com.s206.health.nutrition.favorite.entity.FavoriteFood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteFoodRepository extends JpaRepository<FavoriteFood, Integer> {
    Optional<FavoriteFood> findByUserIdAndFoodFoodId(Integer userId, Integer foodId);
    List<FavoriteFood> findAllByUserId(Integer userId);
    boolean existsByUserIdAndFoodFoodId(Integer userId, Integer foodId);

}