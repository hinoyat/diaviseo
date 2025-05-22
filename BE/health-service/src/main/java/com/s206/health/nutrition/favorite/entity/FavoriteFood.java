package com.s206.health.nutrition.favorite.entity;

import com.s206.health.nutrition.food.entity.Food;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "favorite_food_tb",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "food_id"})})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer favoriteId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
}