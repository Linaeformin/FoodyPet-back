package org.example.foodypet.domain.water.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet_water_intake_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetWaterIntakeItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "water_intake_id", nullable = false)
    private PetWaterIntake waterIntake;

    @Column(name = "amount_ml", precision = 8, scale = 2, nullable = false)
    private BigDecimal amountMl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
