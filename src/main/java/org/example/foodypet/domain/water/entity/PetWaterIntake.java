package org.example.foodypet.domain.water.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.foodypet.common.entity.BaseTimeEntity;
import org.example.foodypet.domain.pet.entity.Pet;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "pet_water_intakes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"pet_id", "intake_date"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetWaterIntake extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "intake_date", nullable = false)
    private LocalDate intakeDate;

    @Column(name = "total_amount_ml", precision = 8, scale = 2, nullable = false)
    private BigDecimal totalAmountMl = BigDecimal.ZERO;

    public static PetWaterIntake create(Pet pet, LocalDate intakeDate) {
        PetWaterIntake waterIntake = new PetWaterIntake();
        waterIntake.pet = pet;
        waterIntake.intakeDate = intakeDate;
        waterIntake.totalAmountMl = BigDecimal.ZERO;
        return waterIntake;
    }

    public void addAmount(BigDecimal amountMl) {
        if (amountMl == null || amountMl.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        this.totalAmountMl = this.totalAmountMl.add(amountMl);
    }
}