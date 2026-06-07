package org.example.foodypet.domain.diary.repository;

import org.example.foodypet.domain.diary.entity.PetMealDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PetMealDiaryRepository extends JpaRepository<PetMealDiary, Long> {
    boolean existsByPetIdAndDailyDietIdAndPetMealScheduleIdAndDiaryDate(
            Long petId,
            Long dailyDietId,
            Long petMealScheduleId,
            LocalDate diaryDate
    );

    @Query("""
            select md
            from PetMealDiary md
            join fetch md.pet p
            join fetch md.dailyDiet dd
            join fetch md.petMealSchedule pms
            where p.id = :petId
              and md.diaryDate = :diaryDate
            order by pms.mealTime asc
            """)
    List<PetMealDiary> findMealDiariesByPetIdAndDate(
            @Param("petId") Long petId,
            @Param("diaryDate") LocalDate diaryDate
    );

    List<PetMealDiary> findByPet_IdAndDiaryDateOrderByCreatedAtDesc(
            Long petId,
            LocalDate diaryDate
    );

    Integer countByPet_IdAndDiaryDate(
            Long petId,
            LocalDate diaryDate
    );
}