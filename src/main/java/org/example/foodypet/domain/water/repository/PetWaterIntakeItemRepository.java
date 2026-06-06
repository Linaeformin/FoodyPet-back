package org.example.foodypet.domain.water.repository;

import org.example.foodypet.domain.water.entity.PetWaterIntake;
import org.example.foodypet.domain.water.entity.PetWaterIntakeItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetWaterIntakeItemRepository extends JpaRepository<PetWaterIntakeItem, Long> {
    void deleteByWaterIntake(PetWaterIntake waterIntake);
    List<PetWaterIntakeItem> findAllByWaterIntakeOrderByIdAsc(PetWaterIntake waterIntake);
}