package com.s206.health.step.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "step_count_tb")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_count_id", nullable = false)
    private Integer stepCountId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "step_date", nullable = false)
    private LocalDate stepDate;

    @Column(name = "step_count")
    private Integer stepCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
}
