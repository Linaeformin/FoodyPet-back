package org.example.foodypet.domain.diary.entity;

import org.example.foodypet.domain.diet.entity.PetDailyDiet;
import org.example.foodypet.domain.pet.entity.PetMealSchedule;
import org.example.foodypet.common.entity.BaseTimeEntity;
import org.example.foodypet.domain.pet.entity.Pet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pet_meal_diaries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetMealDiary extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "daily_diet_id", nullable = false)
    private PetDailyDiet dailyDiet;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pet_meal_schedule_id", nullable = false)
    private PetMealSchedule petMealSchedule;

    @Column(name = "diary_date", nullable = false)
    private LocalDate diaryDate;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Satisfaction satisfaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_status")
    private MealStatus mealStatus;

    @Column(name = "water_intake_ml", precision = 8, scale = 2)
    private BigDecimal waterIntakeMl;

    @Lob
    private String memo;

    public static PetMealDiary create(
            Pet pet,
            PetDailyDiet dailyDiet,
            PetMealSchedule petMealSchedule,
            LocalDate diaryDate,
            String imageUrl,
            Satisfaction satisfaction,
            MealStatus mealStatus,
            BigDecimal waterIntakeMl,
            String memo
    ) {
        PetMealDiary mealDiary = new PetMealDiary();
        mealDiary.pet = pet;
        mealDiary.dailyDiet = dailyDiet;
        mealDiary.petMealSchedule = petMealSchedule;
        mealDiary.diaryDate = diaryDate;
        mealDiary.imageUrl = imageUrl;
        mealDiary.satisfaction = satisfaction;
        mealDiary.mealStatus = mealStatus;
        mealDiary.waterIntakeMl = waterIntakeMl;
        mealDiary.memo = memo;
        return mealDiary;
    }
}
