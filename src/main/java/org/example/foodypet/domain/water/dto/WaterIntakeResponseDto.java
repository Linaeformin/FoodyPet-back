package org.example.foodypet.domain.water.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.foodypet.domain.water.entity.PetWaterIntake;
import org.example.foodypet.domain.water.entity.PetWaterIntakeItem;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class WaterIntakeResponseDto {

    private Long petId;
    private BigDecimal totalAmountMl;
    private List<WaterIntakeItemResponseDto> waterIntakeItems;

    public static WaterIntakeResponseDto of(
            Long petId,
            BigDecimal totalAmountMl,
            List<PetWaterIntakeItem> items
    ) {
        return new WaterIntakeResponseDto(
                petId,
                totalAmountMl,
                items.stream()
                        .map(WaterIntakeItemResponseDto::from)
                        .toList()
        );
    }

    public static WaterIntakeResponseDto empty(Long petId) {
        return new WaterIntakeResponseDto(
                petId,
                BigDecimal.ZERO,
                List.of()
        );
    }

    @Getter
    @AllArgsConstructor
    public static class WaterIntakeItemResponseDto {

        private Long waterIntakeItemId;
        private BigDecimal amountMl;

        public static WaterIntakeItemResponseDto from(PetWaterIntakeItem item) {
            return new WaterIntakeItemResponseDto(
                    item.getId(),
                    item.getAmountMl()
            );
        }
    }
}