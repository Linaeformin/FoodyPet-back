package org.example.foodypet.domain.pet.repository;

import org.example.foodypet.domain.pet.entity.PetCapsuleIntake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PetCapsuleIntakeRepository extends JpaRepository<PetCapsuleIntake, Long> {

    Optional<PetCapsuleIntake> findByPetCapsuleIdAndIntakeDate(Long petCapsuleId, LocalDate intakeDate);
}