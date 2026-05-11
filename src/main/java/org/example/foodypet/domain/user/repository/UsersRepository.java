package org.example.foodypet.domain.user.repository;

import org.example.foodypet.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Long> {
    boolean existsByUserId(String userId);
    Optional<User> findByUserId(String userId);
}