package org.example.foodypet.domain.pet.repository;

import org.example.foodypet.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
    boolean existsByUserId(Long userId);
    Optional<Pet> findByIdAndUserId(Long petId, Long userId);

    Optional<Pet> findByIdAndUser_Id(Long petId, Long userId);
    List<Pet> findByUser_IdOrderByIdAsc(Long userId);
}