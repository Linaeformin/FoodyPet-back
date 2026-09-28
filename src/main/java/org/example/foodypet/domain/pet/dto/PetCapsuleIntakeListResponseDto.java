package org.example.foodypet.domain.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.foodypet.domain.pet.entity.PetCapsule;
import org.example.foodypet.domain.pet.entity.PetCapsuleIntake;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class PetCapsuleIntakeListResponseDto {

    private Long petId;
    private List<PetCapsuleIntakeResponseDto> capsules;

    public static PetCapsuleIntakeListResponseDto of(
            Long petId,
            List<PetCapsule> petCapsules,
            Map<Long, PetCapsuleIntake> intakeMap
    ) {
        List<PetCapsuleIntakeResponseDto> capsules = petCapsules.stream()
                .map(petCapsule -> {
                    PetCapsuleIntake intake = intakeMap.get(petCapsule.getId());

                    return PetCapsuleIntakeResponseDto.of(
                            petCapsule,
                            intake
                    );
                })
                .toList();

        return new PetCapsuleIntakeListResponseDto(petId, capsules);
    }

    @Getter
    @AllArgsConstructor
    public static class PetCapsuleIntakeResponseDto {

        private Long petCapsuleId;
        private String capsuleName;
        private Integer capsuleCount;
        private Integer givenCount;

        public static PetCapsuleIntakeResponseDto of(
                PetCapsule petCapsule,
                PetCapsuleIntake intake
        ) {
            return new PetCapsuleIntakeResponseDto(
                    petCapsule.getId(),
                    petCapsule.getCapsuleName(),
                    petCapsule.getCapsuleCount(),
                    intake == null ? 0 : intake.getGivenCount()
            );
        }
    }
}