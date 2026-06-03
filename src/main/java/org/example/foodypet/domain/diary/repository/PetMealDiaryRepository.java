package org.example.foodypet.domain.diary.repository;

import org.example.foodypet.domain.diary.entity.PetMealDiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PetMealDiaryRepository extends JpaRepository<PetMealDiary, Long> {
    boolean existsByPetIdAndDailyDietIdAndPetMealScheduleIdAndDiaryDate(
            Long petId,
            Long dailyDietId,
            Long petMealScheduleId,
            LocalDate diaryDate
    );
}