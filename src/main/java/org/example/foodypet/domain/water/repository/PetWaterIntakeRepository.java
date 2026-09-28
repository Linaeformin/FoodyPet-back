package org.example.foodypet.domain.water.repository;

import org.example.foodypet.domain.pet.entity.Pet;
import org.example.foodypet.domain.water.entity.PetWaterIntake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PetWaterIntakeRepository extends JpaRepository<PetWaterIntake, Long> {

    Optional<PetWaterIntake> findByPetAndIntakeDate(Pet pet, LocalDate intakeDate);
    Optional<PetWaterIntake> findByPetIdAndIntakeDate(Long petId, LocalDate intakeDate);
    Optional<PetWaterIntake> findByPet_IdAndIntakeDate(
            Long petId,
            LocalDate intakeDate
    );

}