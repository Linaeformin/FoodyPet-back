package org.example.foodypet.domain.food.repository;

import org.example.foodypet.domain.food.entity.PetFoodStock;
import org.example.foodypet.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Collection;

public interface PetFoodStockRepository extends JpaRepository<PetFoodStock, Long> {

    List<PetFoodStock> findByUser(User user);
    List<PetFoodStock> findByUserId(Long userId);
    List<PetFoodStock> findByIdInAndUserId(Collection<Long> stockIds, Long userId);
}