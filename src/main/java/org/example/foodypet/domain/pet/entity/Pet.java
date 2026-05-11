package org.example.foodypet.domain.pet.entity;

import org.example.foodypet.common.entity.BaseTimeEntity;
import org.example.foodypet.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "pets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pet_name", length = 30, nullable = false)
    private String petName;

    @Column(nullable = false)
    private LocalDate birth;

    @Lob
    @Column(name = "pet_img", nullable = false)
    private String petImg;

    @Enumerated(EnumType.STRING)
    @Column(name = "pet_type", nullable = false)
    private PetType petType;

    @Enumerated(EnumType.STRING)
    @Column(name = "dog_breed")
    private DogBreed dogBreed;

    @Enumerated(EnumType.STRING)
    @Column(name = "cat_breed")
    private CatBreed catBreed;

    @Enumerated(EnumType.STRING)
    @Column(name = "pet_gender", nullable = false)
    private PetGender petGender;

    @Enumerated(EnumType.STRING)
    @Column(name = "neutered_status", nullable = false)
    private NeuteredStatus neuteredStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
