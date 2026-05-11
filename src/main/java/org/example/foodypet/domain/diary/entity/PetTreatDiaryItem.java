package org.example.foodypet.domain.diary.entity;

import org.example.foodypet.domain.food.entity.PetFood;
import org.example.foodypet.domain.food.entity.Unit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet_treat_diary_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetTreatDiaryItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "treat_diary_id", nullable = false)
    private PetTreatDiary treatDiary;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pet_food_id", nullable = false)
    private PetFood petFood;

    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit unit;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
