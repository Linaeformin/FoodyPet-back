package org.example.foodypet.domain.community.service;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.S3Uploader;
import org.example.foodypet.domain.community.dto.*;
import org.example.foodypet.domain.community.entity.CommunityPost;
import org.example.foodypet.domain.community.entity.CommunityPostImage;
import org.example.foodypet.domain.community.repository.CommunityPostImageRepository;
import org.example.foodypet.domain.community.repository.CommunityPostRepository;
import org.example.foodypet.domain.diary.entity.PetMealDiary;
import org.example.foodypet.domain.diary.repository.PetMealDiaryRepository;
import org.example.foodypet.domain.diet.entity.PetDailyDietItem;
import org.example.foodypet.domain.diet.repository.PetDailyDietItemRepository;
import org.example.foodypet.domain.pet.entity.Pet;
import org.example.foodypet.domain.pet.entity.PetType;
import org.example.foodypet.domain.pet.repository.PetRepository;
import org.example.foodypet.domain.user.entity.User;
import org.example.foodypet.domain.user.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {

    private final UsersRepository usersRepository;
    private final CommunityPostRepository communityPostRepository;
    private final CommunityPostImageRepository communityPostImageRepository;
    private final S3Uploader s3Uploader;
    private final PetRepository petRepository;
    private final PetMealDiaryRepository petMealDiaryRepository;
    private final PetDailyDietItemRepository petDailyDietItemRepository;

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    public List<ConnectMealTimeResponseDto> getConnectMealTimes(
            Long userPk,
            ConnectMealTimeRequestDto request
    ) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("반려동물을 찾을 수 없습니다."));

        validatePetOwner(pet, userPk);

        List<PetMealDiary> mealDiaries =
                petMealDiaryRepository.findByPet_IdAndDiaryDateOrderByCreatedAtDesc(
                        request.getPetId(),
                        request.getMealDate()
                );

        return mealDiaries.stream()
                .sorted(Comparator.comparing(
                        mealDiary -> mealDiary.getPetMealSchedule().getMealTime()
                ))
                .map(mealDiary -> new ConnectMealTimeResponseDto(
                        mealDiary.getPetMealSchedule()
                                .getMealTime()
                                .format(TIME_FORMATTER)
                ))
                .toList();
    }

    private void validatePetOwner(Pet pet, Long userPk) {
        if (!pet.getUser().getId().equals(userPk)) {
            throw new IllegalArgumentException("해당 반려동물에 접근할 수 없습니다.");
        }
    }

    public ConnectMealPreviewResponseDto getConnectMealPreview(
            Long userPk,
            ConnectMealPreviewRequestDto request
    ) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("반려동물을 찾을 수 없습니다."));

        validatePetOwner(pet, userPk);

        List<PetMealDiary> mealDiaries =
                petMealDiaryRepository.findByPet_IdAndDiaryDateOrderByCreatedAtDesc(
                        request.getPetId(),
                        request.getMealDate()
                );

        PetMealDiary mealDiary = mealDiaries.stream()
                .filter(diary -> diary.getPetMealSchedule()
                        .getMealTime()
                        .equals(request.getMealTime()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 급여 시각의 식단 일기를 찾을 수 없습니다."));

        Long dailyDietId = mealDiary.getDailyDiet().getId();
        Integer mealOrder = mealDiary.getPetMealSchedule().getMealOrder();

        List<PetDailyDietItem> dietItems =
                petDailyDietItemRepository.findByDailyDiet_IdAndMealOrderOrderByIdAsc(
                        dailyDietId,
                        mealOrder
                );

        List<ConnectMealFoodResponseDto> foods = dietItems.stream()
                .map(ConnectMealFoodResponseDto::from)
                .toList();

        return new ConnectMealPreviewResponseDto(
                mealDiary.getId(),
                dailyDietId,
                mealDiary.getImageUrl(),
                foods
        );
    }

    @Transactional
    public CommunityPostCreateResponseDto createCommunityPost(
            Long userPk,
            MultipartFile image,
            CommunityPostCreateRequestDto request
    ) {
        User user = usersRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        PetMealDiary mealDiary = petMealDiaryRepository.findById(request.getMealDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("식단 일기를 찾을 수 없습니다."));

        validatePetOwner(mealDiary.getPet(), userPk);

        String imageUrl = s3Uploader.uploadCommunityPostImage(image);

        CommunityPost post = CommunityPost.create(
                user,
                mealDiary,
                request.getTitle(),
                request.getContent()
        );

        CommunityPost savedPost = communityPostRepository.save(post);

        CommunityPostImage postImage = CommunityPostImage.create(
                savedPost,
                imageUrl,
                1
        );

        communityPostImageRepository.save(postImage);

        return new CommunityPostCreateResponseDto(savedPost.getId());
    }

    public List<CommunityPostListResponseDto> getCommunityPosts(Long userPk) {
        List<CommunityPost> posts = communityPostRepository.findAllForList();

        Random random = new Random();

        return posts.stream()
                .map(post -> {
                    Long dailyDietId = post.getMealDiary().getDailyDiet().getId();
                    Integer mealOrder = post.getMealDiary().getPetMealSchedule().getMealOrder();

                    List<PetDailyDietItem> dietItems =
                            petDailyDietItemRepository.findByDailyDiet_IdAndMealOrderOrderByIdAsc(
                                    dailyDietId,
                                    mealOrder
                            );

                    List<CommunityPostMealResponseDto> meal = dietItems.stream()
                            .map(item -> new CommunityPostMealResponseDto(
                                    item.getPetFood().getName(),
                                    item.getAmount(),
                                    item.getUnit()
                            ))
                            .toList();

                    String imgUrl = communityPostImageRepository
                            .findFirstByPost_IdOrderByImageOrderAsc(post.getId())
                            .map(CommunityPostImage::getImageUrl)
                            .orElse(null);

                    Integer likeCount = random.nextInt(200);
                    Integer commentCount = random.nextInt(50);

                    Boolean isMine = post.getUser().getId().equals(userPk);

                    return new CommunityPostListResponseDto(
                            post.getId(),
                            post.getUser().getUserImg(),
                            post.getUser().getNickname(),
                            convertPetTypeToTag(post.getMealDiary().getPet().getPetType()),
                            post.getTitle(),
                            imgUrl,
                            post.getContent(),
                            meal,
                            likeCount,
                            commentCount,
                            isMine
                    );
                })
                .toList();
    }

    private String convertPetTypeToTag(PetType petType) {
        if (petType == null) {
            return "";
        }

        return switch (petType) {
            case CAT -> "고양이";
            case DOG -> "강아지";
        };
    }
}