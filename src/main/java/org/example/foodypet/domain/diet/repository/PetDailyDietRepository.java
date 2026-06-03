package org.example.foodypet.domain.diet.repository;

import org.example.foodypet.domain.diet.entity.PetDailyDiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PetDailyDietRepository extends JpaRepository<PetDailyDiet, Long> {

    Optional<PetDailyDiet> findByPet_IdAndDietDate(Long petId, LocalDate dietDate);

    boolean existsByPet_IdAndDietDateAndIsConfirmedTrue(Long petId, LocalDate dietDate);
}