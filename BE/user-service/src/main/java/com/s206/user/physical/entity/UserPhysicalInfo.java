package com.s206.user.physical.entity;

import com.s206.user.user.entity.User;
import com.s206.user.user.enums.Goal;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "user_physical_info_tb",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"})
)
public class UserPhysicalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPhysicalInfoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal height;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal weight;

    @Column(nullable = false)
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Goal goal;

    @Column(nullable = false)
    private LocalDate date;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void update(BigDecimal height, BigDecimal weight, LocalDate birthday, Goal goal) {
        this.height = height;
        this.weight = weight;
        this.birthday = birthday;
        this.goal = goal;
    }
}
