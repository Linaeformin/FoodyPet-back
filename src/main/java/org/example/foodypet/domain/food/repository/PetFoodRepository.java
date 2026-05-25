package org.example.foodypet.domain.food.repository;

import org.example.foodypet.domain.food.entity.PetFood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetFoodRepository extends JpaRepository<PetFood, Long> {
    List<PetFood> findTop10ByNameContaining(String keyword);
    List<PetFood> findTop3ByNameContaining(String keyword);
}