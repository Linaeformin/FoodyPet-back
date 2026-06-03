package org.example.foodypet.domain.diary.repository;

import org.example.foodypet.domain.diary.entity.PetMealDiaryCapsule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetMealDiaryCapsuleRepository extends JpaRepository<PetMealDiaryCapsule, Long> {
}