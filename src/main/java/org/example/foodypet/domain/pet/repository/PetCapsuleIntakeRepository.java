package org.example.foodypet.domain.pet.repository;

import org.example.foodypet.domain.pet.entity.PetCapsuleIntake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PetCapsuleIntakeRepository extends JpaRepository<PetCapsuleIntake, Long> {

    Optional<PetCapsuleIntake> findByPetCapsuleIdAndIntakeDate(Long petCapsuleId, LocalDate intakeDate);
    List<PetCapsuleIntake> findAllByPetCapsulePetIdAndIntakeDate(
            Long petId,
            LocalDate intakeDate
    );
}