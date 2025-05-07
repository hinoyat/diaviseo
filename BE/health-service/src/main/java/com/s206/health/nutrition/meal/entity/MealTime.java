package com.s206.health.nutrition.meal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "meal_time_tb")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mealTimeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MealType mealType; // 아침, 점심, 저녁, 간식

    @OneToMany(mappedBy = "mealTime", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<MealFood> mealFoods = new ArrayList<>();

    @Column(nullable = false)
    private LocalTime eatingTime;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    public void updateEatingTime(LocalTime eatingTime) {
        this.eatingTime = eatingTime;
    }
    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
