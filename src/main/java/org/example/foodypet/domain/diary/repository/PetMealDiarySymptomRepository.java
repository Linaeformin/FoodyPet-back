package org.example.foodypet.domain.diary.repository;

import org.example.foodypet.domain.diary.entity.PetMealDiarySymptom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetMealDiarySymptomRepository extends JpaRepository<PetMealDiarySymptom, Long> {
}