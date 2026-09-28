package org.example.foodypet.domain.pet.repository;

import org.example.foodypet.domain.pet.entity.PetMealSchedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PetMealScheduleRepository extends JpaRepository<PetMealSchedule, Long> {
    List<PetMealSchedule> findByPetIdOrderByMealOrderAsc(Long petId);
    @Query("""
        select s
        from PetMealSchedule s
        where s.pet.id = :petId
          and not exists (
              select 1
              from PetMealDiary md
              where md.petMealSchedule.id = s.id
                and md.diaryDate = :today
          )
        order by s.mealTime asc
    """)
    List<PetMealSchedule> findOldestNotWrittenSchedule(
            @Param("petId") Long petId,
            @Param("today") LocalDate today,
            Pageable pageable
    );

    Integer countByPet_Id(Long petId);

    @Query("""
        select s
        from PetMealSchedule s
        where s.pet.id = :petId
          and not exists (
              select 1
              from PetMealDiary md
              where md.pet.id = :petId
                and md.dailyDiet.id = :dailyDietId
                and md.petMealSchedule.id = s.id
                and md.diaryDate = :today
          )
        order by s.mealTime asc
    """)
    List<PetMealSchedule> findOldestNotWrittenSchedule(
            @Param("petId") Long petId,
            @Param("dailyDietId") Long dailyDietId,
            @Param("today") LocalDate today,
            Pageable pageable
    );
}