package org.example.foodypet.domain.diary.repository;

import org.example.foodypet.domain.diary.entity.PetMealDiary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetMealDiaryRepository extends JpaRepository<PetMealDiary, Long> {
}