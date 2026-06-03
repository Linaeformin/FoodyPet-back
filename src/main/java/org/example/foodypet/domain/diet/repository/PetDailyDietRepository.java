package org.example.foodypet.domain.diet.repository;

import org.example.foodypet.domain.diet.entity.PetDailyDiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface PetDailyDietRepository extends JpaRepository<PetDailyDiet, Long> {

    Optional<PetDailyDiet> findByPet_IdAndDietDate(Long petId, LocalDate dietDate);

    boolean existsByPet_IdAndDietDateAndIsConfirmedTrue(Long petId, LocalDate dietDate);

    Optional<PetDailyDiet> findByPetIdAndDietDateAndIsConfirmedTrue(
            Long petId,
            LocalDate dietDate
    );

    @Query("""
        select d
        from PetDailyDiet d
        where d.pet.id = :petId
          and d.dietDate = :dietDate
          and d.isConfirmed = true
          and not exists (
              select 1
              from PetMealDiary md
              where md.dailyDiet = d
          )
    """)
    Optional<PetDailyDiet> findAvailableConfirmedDiet(
            @Param("petId") Long petId,
            @Param("dietDate") LocalDate dietDate
    );
}