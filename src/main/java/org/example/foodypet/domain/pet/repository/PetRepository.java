package org.example.foodypet.domain.pet.repository;

import org.example.foodypet.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
    boolean existsByUserId(Long userId);
}