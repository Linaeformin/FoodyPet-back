package org.example.foodypet.domain.water.repository;

import org.example.foodypet.domain.water.entity.PetWaterIntakeItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetWaterIntakeItemRepository extends JpaRepository<PetWaterIntakeItem, Long> {
}