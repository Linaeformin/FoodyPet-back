package org.example.foodypet.domain.diet.repository;

import org.example.foodypet.domain.diet.entity.PetDailyDiet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetDailyDietRepository extends JpaRepository<PetDailyDiet, Long> {
}