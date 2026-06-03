package org.example.foodypet.domain.diet.repository;

import org.example.foodypet.domain.diet.entity.PetDailyDietItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetDailyDietItemRepository extends JpaRepository<PetDailyDietItem, Long> {
}