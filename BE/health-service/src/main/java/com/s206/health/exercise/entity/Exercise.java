package com.s206.health.exercise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exercise_tb")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Integer exerciseId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "exercise_type_id", nullable = false)
    private Integer exerciseTypeId;

    @Column(name = "exercise_date", nullable = false)
    private LocalDateTime exerciseDate;

    @Column(name = "exercise_time", nullable = false)
    private Integer exerciseTime;

    @Column(name = "exercise_calorie", nullable = false)
    private Integer exerciseCalorie;

    @Column(name = "health_connect_uuid ", nullable = true)
    private String healthConnectUuid;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
}
