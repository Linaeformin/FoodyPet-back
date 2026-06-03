package org.example.foodypet.domain.diet.repository;

import org.example.foodypet.domain.diet.entity.PetDailyDietItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PetDailyDietItemRepository extends JpaRepository<PetDailyDietItem, Long> {

    void deleteByDailyDiet_Id(Long dailyDietId);

    @Query("""
        select distinct i.petFood.id
        from PetDailyDietItem i
        where i.dailyDiet.id = :dietId
    """)
    List<Long> findPetFoodIdsByDailyDietId(@Param("dietId") Long dietId);

    List<PetDailyDietItem> findByDailyDiet_Id(Long dailyDietId);

    List<PetDailyDietItem> findByDailyDietIdOrderByMealOrderAscIdAsc(Long dailyDietId);
}