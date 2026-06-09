package org.example.foodypet.domain.diary.repository;

import org.example.foodypet.domain.diary.entity.PetMealDiaryCapsule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetMealDiaryCapsuleRepository extends JpaRepository<PetMealDiaryCapsule, Long> {

    List<PetMealDiaryCapsule> findByMealDiary_Id(Long mealDiaryId);
}