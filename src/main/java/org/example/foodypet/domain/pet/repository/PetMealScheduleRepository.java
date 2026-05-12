package org.example.foodypet.domain.pet.repository;

import org.example.foodypet.domain.pet.entity.PetMealSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetMealScheduleRepository extends JpaRepository<PetMealSchedule, Long> {
}