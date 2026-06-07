package org.example.foodypet.domain.food.repository;

import org.example.foodypet.domain.food.entity.PetFoodStock;
import org.example.foodypet.domain.pet.entity.PetType;
import org.example.foodypet.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Collection;
import java.util.Optional;

public interface PetFoodStockRepository extends JpaRepository<PetFoodStock, Long> {

    List<PetFoodStock> findByUser(User user);
    List<PetFoodStock> findByUserId(Long userId);
    List<PetFoodStock> findByIdInAndUserId(Collection<Long> stockIds, Long userId);
    Optional<PetFoodStock> findByIdAndUserId(Long stockId, Long userId);

    @EntityGraph(attributePaths = {"petFood"})
    List<PetFoodStock> findByUserIdAndPetFoodPetType(Long userId, PetType petType);

    List<PetFoodStock> findByUser_IdAndPetFood_IdIn(Long userId, List<Long> petFoodIds);
    @EntityGraph(attributePaths = {"petFood"})
    List<PetFoodStock> findByUserIdAndIsTreatTrueAndPetFood_NameContainingIgnoreCase(
            Long userId,
            String keyword
    );

    @EntityGraph(attributePaths = {"petFood"})
    Optional<PetFoodStock> findByIdAndUserIdAndIsTreatTrue(
            Long stockId,
            Long userId
    );
}