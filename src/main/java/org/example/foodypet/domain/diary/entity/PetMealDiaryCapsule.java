package org.example.foodypet.domain.diary.entity;

import org.example.foodypet.domain.pet.entity.PetCapsule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "pet_meal_diary_capsules",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"meal_diary_id", "pet_capsule_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetMealDiaryCapsule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_diary_id", nullable = false)
    private PetMealDiary mealDiary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_capsule_id", nullable = false)
    private PetCapsule petCapsule;

    @Column(name = "given_count", nullable = false)
    private Integer givenCount = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public static PetMealDiaryCapsule create(
            PetMealDiary mealDiary,
            PetCapsule petCapsule,
            Integer givenCount
    ) {
        PetMealDiaryCapsule diaryCapsule = new PetMealDiaryCapsule();
        diaryCapsule.mealDiary = mealDiary;
        diaryCapsule.petCapsule = petCapsule;
        diaryCapsule.givenCount = givenCount == null ? 0 : givenCount;
        return diaryCapsule;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}