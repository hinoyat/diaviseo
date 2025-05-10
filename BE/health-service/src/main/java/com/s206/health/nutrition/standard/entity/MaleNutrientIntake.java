package com.s206.health.nutrition.standard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "male_nutrient_intake")
@Getter
@Setter
@NoArgsConstructor
public class MaleNutrientIntake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgeGroup ageGroup;

    // 총 칼로리 섭취량(kcal)
    @Column(nullable = false)
    private Double calories;

    // 단백질 섭취량(g)
    @Column(nullable = false)
    private Double protein;

    // 지방 섭취량(g)
    @Column(nullable = false)
    private Double fat;

    // 탄수화물 섭취량(g)
    @Column(nullable = false)
    private Double carbohydrates;

    // 당 섭취량(g)
    @Column(nullable = false)
    private Double sugar;

    // 나트륨 섭취량(mg)
    @Column(nullable = false)
    private Double sodium;
}