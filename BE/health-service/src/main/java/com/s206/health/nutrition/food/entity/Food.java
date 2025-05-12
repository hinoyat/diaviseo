package com.s206.health.nutrition.food.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "food_information_tb")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer foodId;

    @Column(nullable = false, length = 50)
    private String foodName;

    @Column(nullable = false)
    private Integer calorie;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal carbohydrate;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal protein;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal fat;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal sweet;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal sodium;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal saturatedFat;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal transFat;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal cholesterol;

    @Column(nullable = false, precision = 6, scale = 2)
    private String baseAmount;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
}
