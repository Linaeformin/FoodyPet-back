package org.example.foodypet.domain.pet.repository;

import org.example.foodypet.domain.pet.entity.PetNutritionStandard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetNutritionStandardRepository extends JpaRepository<PetNutritionStandard, Long> {
    Optional<PetNutritionStandard> findByPetId(Long petId);
}