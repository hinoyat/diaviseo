package com.s206.health.condition.disease.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_disease_tb",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "disease_id"})})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDisease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id", nullable = false)
    private Disease disease;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
