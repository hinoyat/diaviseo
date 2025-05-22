package com.s206.health.nutrition.foodset.entity;

import com.s206.health.nutrition.food.entity.Food;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "food_set_food_tb")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodSetFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer FoodSetFoodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_set_id", nullable = false)
    private FoodSet foodSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(nullable = false)
    private Float quantity;

}
