package org.example.foodypet.domain.pet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.foodypet.common.entity.BaseTimeEntity;

@Entity
@Table(name = "pet_capsules")
@Getter
@Setter
@NoArgsConstructor
public class PetCapsule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "capsule_name", length = 30, nullable = false)
    private String capsuleName;

    @Column(name = "capsule_count", nullable = false)
    private Integer capsuleCount;
}