package com.s206.health.condition.allergy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_allergy_tb",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "allergy_id"})})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAllergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allergy_id", nullable = false)
    private FoodAllergy allergy;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
