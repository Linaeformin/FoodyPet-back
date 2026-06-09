package org.example.foodypet.domain.diary.service;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.domain.diary.dto.PetTreatDiaryCreateRequest;
import org.example.foodypet.domain.diary.dto.TreatDiaryItemResponse;
import org.example.foodypet.domain.diary.dto.TreatDiaryTodayResponse;
import org.example.foodypet.domain.diary.dto.TreatStockAutocompleteResponse;
import org.example.foodypet.domain.diary.entity.PetTreatDiary;
import org.example.foodypet.domain.diary.entity.PetTreatDiaryItem;
import org.example.foodypet.domain.diary.repository.PetTreatDiaryItemRepository;
import org.example.foodypet.domain.diary.repository.PetTreatDiaryRepository;
import org.example.foodypet.domain.food.entity.PetFoodStock;
import org.example.foodypet.domain.food.repository.PetFoodStockRepository;
import org.example.foodypet.domain.pet.entity.Pet;
import org.example.foodypet.domain.pet.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetTreatDiaryService {

    private final PetRepository petRepository;
    private final PetFoodStockRepository petFoodStockRepository;
    private final PetTreatDiaryRepository petTreatDiaryRepository;
    private final PetTreatDiaryItemRepository petTreatDiaryItemRepository;

    public List<TreatStockAutocompleteResponse> autocompleteTreatStocks(
            Long userId,
            String keyword
    ) {
        String safeKeyword = keyword == null ? "" : keyword.trim();

        return petFoodStockRepository
                .findByUserIdAndIsTreatTrueAndPetFood_NameContainingIgnoreCase(userId, safeKeyword)
                .stream()
                .map(TreatStockAutocompleteResponse::from)
                .toList();
    }

    public List<TreatDiaryTodayResponse> getTodayTreatDiaries(
            Long userId,
            Long petId
    ) {
        validatePetOwner(userId, petId);

        LocalDate today = LocalDate.now();

        List<PetTreatDiary> diaries =
                petTreatDiaryRepository.findByPetIdAndDiaryDateOrderByTreatRoundAsc(petId, today);

        if (diaries.isEmpty()) {
            return List.of();
        }

        List<Long> diaryIds = diaries.stream()
                .map(PetTreatDiary::getId)
                .toList();

        List<PetTreatDiaryItem> items =
                petTreatDiaryItemRepository.findByTreatDiaryIdInOrderByTreatDiaryTreatRoundAscIdAsc(diaryIds);

        Map<Integer, List<TreatDiaryItemResponse>> groupedItems = items.stream()
                .collect(
                        LinkedHashMap::new,
                        (map, item) -> {
                            Integer treatRound = item.getTreatDiary().getTreatRound();

                            map.computeIfAbsent(treatRound, key -> new java.util.ArrayList<>())
                                    .add(TreatDiaryItemResponse.from(item));
                        },
                        Map::putAll
                );

        return groupedItems.entrySet()
                .stream()
                .map(entry -> new TreatDiaryTodayResponse(
                        entry.getKey(),
                        entry.getValue()
                ))
                .toList();
    }

    @Transactional
    public void createTreatDiary(
            Long userId,
            Long petId,
            PetTreatDiaryCreateRequest request
    ) {
        validateCreateRequest(request);

        Pet pet = petRepository.findByIdAndUserId(petId, userId)
                .orElseThrow(() -> new IllegalArgumentException("반려동물을 찾을 수 없습니다."));

        LocalDate today = LocalDate.now();

        boolean alreadyExists = petTreatDiaryRepository.existsByPetIdAndDiaryDateAndTreatRound(
                petId,
                today,
                request.treatRound()
        );

        if (alreadyExists) {
            throw new IllegalArgumentException("이미 등록된 간식 회차입니다.");
        }

        PetTreatDiary treatDiary = PetTreatDiary.create(
                pet,
                today,
                request.treatRound()
        );

        petTreatDiaryRepository.save(treatDiary);

        List<PetTreatDiaryItem> items = request.items()
                .stream()
                .map(itemRequest -> {
                    PetFoodStock stock = petFoodStockRepository
                            .findByIdAndUserIdAndIsTreatTrue(itemRequest.stockId(), userId)
                            .orElseThrow(() -> new IllegalArgumentException("간식 재고를 찾을 수 없습니다."));

                    return PetTreatDiaryItem.create(
                            treatDiary,
                            stock.getPetFood(),
                            itemRequest.amount(),
                            itemRequest.unit()
                    );
                })
                .toList();

        petTreatDiaryItemRepository.saveAll(items);
    }

    private void validatePetOwner(Long userId, Long petId) {
        boolean exists = petRepository.findByIdAndUserId(petId, userId).isPresent();

        if (!exists) {
            throw new IllegalArgumentException("반려동물을 찾을 수 없습니다.");
        }
    }

    private void validateCreateRequest(PetTreatDiaryCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("간식 등록 요청이 비어 있습니다.");
        }

        if (request.treatRound() == null || request.treatRound() <= 0) {
            throw new IllegalArgumentException("간식 회차를 올바르게 입력해야 합니다.");
        }

        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("간식 목록을 1개 이상 입력해야 합니다.");
        }

        for (PetTreatDiaryCreateRequest.Item item : request.items()) {
            if (item.stockId() == null) {
                throw new IllegalArgumentException("간식 재고를 선택해야 합니다.");
            }

            if (item.amount() == null || item.amount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("간식 급여량은 0보다 커야 합니다.");
            }

            if (item.unit() == null) {
                throw new IllegalArgumentException("간식 단위를 선택해야 합니다.");
            }
        }
    }
}