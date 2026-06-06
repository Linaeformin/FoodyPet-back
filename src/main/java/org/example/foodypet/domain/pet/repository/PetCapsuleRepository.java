package org.example.foodypet.domain.pet.repository;

import org.example.foodypet.domain.pet.entity.PetCapsule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetCapsuleRepository extends JpaRepository<PetCapsule, Long> {
    List<PetCapsule> findByPetId(Long petId);
    Optional<PetCapsule> findByIdAndPetId(Long id, Long petId);
    List<PetCapsule> findAllByPetIdOrderByIdAsc(Long petId);
}