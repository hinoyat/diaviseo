package com.s206.health.nutrition.foodset.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "food_set_tb")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer FoodSetId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "foodSet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FoodSetFood> foodSetFoods = new ArrayList<>();

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

    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void updateName(String name) {
        this.name = name;
    }

}
