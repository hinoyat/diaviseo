package com.s206.health.nutrition.standard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "male_nutrient_standard")
@Getter
@Setter
@NoArgsConstructor
public class MaleNutrientStandard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgeGroup ageGroup;

    // 칼로리 (kcal)
    @Column(nullable = false)
    private Integer caloriesMin;

    @Column
    private Integer caloriesMax;

    // 탄수화물 (g)
    @Column(nullable = false)
    private Integer carbohydratesMin;

    @Column
    private Integer carbohydratesMax;

    // 단백질 (g)
    @Column(nullable = false)
    private Integer proteinMin;

    @Column
    private Integer proteinMax;

    // 지방 (g)
    @Column(nullable = false)
    private Double fatMin;

    @Column
    private Double fatMax;

    // 나트륨 (mg)
    @Column(nullable = false)
    private Integer sodium;
}