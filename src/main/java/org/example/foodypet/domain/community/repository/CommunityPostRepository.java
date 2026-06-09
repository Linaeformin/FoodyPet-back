package org.example.foodypet.domain.community.repository;

import org.example.foodypet.domain.community.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    @Query("""
            select p
            from CommunityPost p
            join fetch p.user u
            join fetch p.mealDiary md
            join fetch md.pet pet
            join fetch md.dailyDiet dd
            join fetch md.petMealSchedule pms
            order by p.createdAt desc
            """)
    List<CommunityPost> findAllForList();
}