package org.example.foodypet.domain.diary.repository;

import org.example.foodypet.domain.diary.entity.PetTreatDiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PetTreatDiaryRepository extends JpaRepository<PetTreatDiary, Long> {

    boolean existsByPetIdAndDiaryDateAndTreatRound(
            Long petId,
            LocalDate diaryDate,
            Integer treatRound
    );

    List<PetTreatDiary> findByPetIdAndDiaryDateOrderByTreatRoundAsc(
            Long petId,
            LocalDate diaryDate
    );
}