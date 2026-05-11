package org.example.foodypet.domain.pet.entity;

import org.example.foodypet.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "pet_capsule_intakes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"pet_capsule_id", "intake_date"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetCapsuleIntake extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_capsule_id", nullable = false)
    private PetCapsule petCapsule;

    @Column(name = "intake_date", nullable = false)
    private LocalDate intakeDate;

    @Column(name = "given_count", nullable = false)
    private Integer givenCount = 0;
}
