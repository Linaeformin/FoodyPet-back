package org.example.foodypet.domain.pet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.foodypet.common.entity.BaseTimeEntity;

import java.time.LocalTime;

@Entity
@Table(name = "pet_meal_schedules", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"pet_id", "meal_order"}),
        @UniqueConstraint(columnNames = {"pet_id", "meal_time"})
})
@Getter
@Setter
@NoArgsConstructor
public class PetMealSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "meal_order", nullable = false)
    private Integer mealOrder;

    @Column(name = "meal_time", nullable = false)
    private LocalTime mealTime;
}