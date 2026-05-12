package org.example.foodypet.domain.pet.repository;

import org.example.foodypet.domain.pet.entity.PetNutritionStandard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetNutritionStandardRepository extends JpaRepository<PetNutritionStandard, Long> {
}