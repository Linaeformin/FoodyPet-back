package org.example.foodypet.domain.food.repository;

import org.example.foodypet.domain.food.entity.PetFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetFoodRepository extends JpaRepository<PetFood, Long> {
}