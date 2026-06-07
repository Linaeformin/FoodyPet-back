package org.example.foodypet.domain.water.repository;

import org.example.foodypet.domain.water.entity.PetWaterIntake;
import org.example.foodypet.domain.water.entity.PetWaterIntakeItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PetWaterIntakeItemRepository extends JpaRepository<PetWaterIntakeItem, Long> {
    void deleteByWaterIntake(PetWaterIntake waterIntake);
    List<PetWaterIntakeItem> findAllByWaterIntakeOrderByIdAsc(PetWaterIntake waterIntake);
}