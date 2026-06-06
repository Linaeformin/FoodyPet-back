package org.example.foodypet.domain.water.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class WaterIntakeRequestDto {

    @NotEmpty(message = "음수량 목록은 필수입니다.")
    private List<
            @NotNull(message = "음수량은 필수입니다.")
            @DecimalMin(value = "0.01", message = "음수량은 0보다 커야 합니다.")
                    BigDecimal
            > amountsMl;
}