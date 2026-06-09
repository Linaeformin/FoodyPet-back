package org.example.foodypet.domain.diary.repository;

import org.example.foodypet.domain.diary.entity.PetMealDiarySymptom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetMealDiarySymptomRepository extends JpaRepository<PetMealDiarySymptom, Long> {

    List<PetMealDiarySymptom> findByMealDiary_Id(Long mealDiaryId);
}