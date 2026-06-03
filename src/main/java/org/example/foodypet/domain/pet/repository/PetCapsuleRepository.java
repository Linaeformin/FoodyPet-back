package org.example.foodypet.domain.pet.repository;

import org.example.foodypet.domain.pet.entity.PetCapsule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetCapsuleRepository extends JpaRepository<PetCapsule, Long> {
    List<PetCapsule> findByPetId(Long petId);
}