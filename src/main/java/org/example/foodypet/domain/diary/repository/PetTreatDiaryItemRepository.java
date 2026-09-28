package org.example.foodypet.domain.diary.repository;

import org.example.foodypet.domain.diary.entity.PetTreatDiaryItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PetTreatDiaryItemRepository extends JpaRepository<PetTreatDiaryItem, Long> {

    @EntityGraph(attributePaths = {"treatDiary", "petFood"})
    List<PetTreatDiaryItem> findByTreatDiaryIdInOrderByTreatDiaryTreatRoundAscIdAsc(
            Collection<Long> treatDiaryIds
    );
}