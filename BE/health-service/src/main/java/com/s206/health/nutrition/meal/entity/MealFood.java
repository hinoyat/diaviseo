package com.s206.health.nutrition.meal.entity;

import com.s206.health.nutrition.food.entity.Food;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "meal_food_tb")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mealFoodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_time_id", nullable = false)
    private MealTime mealTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(nullable = false)
    private Float quantity;

    @Column(length = 250)
    private String foodImageUrl;

    // 음식 이미지 URL 업데이트
    public void updateFoodImageUrl(String foodImageUrl) {
        this.foodImageUrl = foodImageUrl;
    }

    // 음식 수량 업데이트
    public void updateQuantity(Float quantity) {
        this.quantity = quantity;
    }
}
