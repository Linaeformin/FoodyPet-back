package org.example.foodypet.domain.water.service;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.domain.pet.entity.Pet;
import org.example.foodypet.domain.pet.repository.PetRepository;
import org.example.foodypet.domain.water.dto.WaterIntakeRequestDto;
import org.example.foodypet.domain.water.entity.PetWaterIntake;
import org.example.foodypet.domain.water.entity.PetWaterIntakeItem;
import org.example.foodypet.domain.water.repository.PetWaterIntakeItemRepository;
import org.example.foodypet.domain.water.repository.PetWaterIntakeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WaterIntakeService {

    private final PetRepository petRepository;
    private final PetWaterIntakeRepository petWaterIntakeRepository;
    private final PetWaterIntakeItemRepository petWaterIntakeItemRepository;

    public void updateTodayWaterIntake(Long userId, Long petId, WaterIntakeRequestDto requestDto) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반려동물을 찾을 수 없습니다."));

        LocalDate today = LocalDate.now();

        PetWaterIntake waterIntake = petWaterIntakeRepository
                .findByPetIdAndIntakeDate(petId, today)
                .orElseGet(() -> petWaterIntakeRepository.save(
                        PetWaterIntake.create(pet, today)
                ));

        petWaterIntakeItemRepository.deleteByWaterIntake(waterIntake);

        BigDecimal totalAmount = requestDto.getAmountsMl()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        waterIntake.updateTotalAmount(totalAmount);

        List<PetWaterIntakeItem> items = requestDto.getAmountsMl()
                .stream()
                .map(amountMl -> PetWaterIntakeItem.create(waterIntake, amountMl))
                .toList();

        petWaterIntakeItemRepository.saveAll(items);
    }
}