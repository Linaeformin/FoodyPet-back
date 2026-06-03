package org.example.foodypet.domain.pet.repository;

import org.example.foodypet.domain.pet.entity.PetMealSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetMealScheduleRepository extends JpaRepository<PetMealSchedule, Long> {
    List<PetMealSchedule> findByPetIdOrderByMealOrderAsc(Long petId);
}