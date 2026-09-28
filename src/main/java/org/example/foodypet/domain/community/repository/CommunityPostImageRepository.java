package org.example.foodypet.domain.community.repository;

import org.example.foodypet.domain.community.entity.CommunityPostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityPostImageRepository extends JpaRepository<CommunityPostImage, Long> {

    Optional<CommunityPostImage> findFirstByPost_IdOrderByImageOrderAsc(Long postId);
}