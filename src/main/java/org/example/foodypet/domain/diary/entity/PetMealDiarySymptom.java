package org.example.foodypet.domain.diary.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pet_meal_diary_symptoms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetMealDiarySymptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_diary_id", nullable = false)
    private PetMealDiary mealDiary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Symptom symptom;

    public static PetMealDiarySymptom create(
            PetMealDiary mealDiary,
            Symptom symptom
    ) {
        PetMealDiarySymptom diarySymptom = new PetMealDiarySymptom();
        diarySymptom.mealDiary = mealDiary;
        diarySymptom.symptom = symptom;
        return diarySymptom;
    }
}