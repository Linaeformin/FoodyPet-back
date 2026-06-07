package org.example.foodypet.domain.diary.entity;

import org.example.foodypet.common.entity.BaseTimeEntity;
import org.example.foodypet.domain.pet.entity.Pet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "pet_treat_diaries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetTreatDiary extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "diary_date", nullable = false)
    private LocalDate diaryDate;

    @Column(name = "treat_round", nullable = false)
    private Integer treatRound;

    public static PetTreatDiary create(
            Pet pet,
            LocalDate diaryDate,
            Integer treatRound
    ) {
        PetTreatDiary diary = new PetTreatDiary();
        diary.pet = pet;
        diary.diaryDate = diaryDate;
        diary.treatRound = treatRound;
        return diary;
    }
}
