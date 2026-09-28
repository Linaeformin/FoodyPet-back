package org.example.foodypet.domain.food.service;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.domain.food.entity.PetFood;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PetFoodAutocompleteRedisService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String KEY = "autocomplete:foods";

    public void savePetFood(PetFood petFood) {
        if (petFood == null || petFood.getName() == null || petFood.getName().isBlank()) {
            return;
        }

        stringRedisTemplate.opsForZSet()
                .add(KEY, petFood.getName().trim(), 0);
    }

    public List<String> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        String trimmedKeyword = keyword.trim();

        Set<String> result = stringRedisTemplate.opsForZSet()
                .rangeByLex(
                        KEY,
                        Range.closed(trimmedKeyword, trimmedKeyword + "\uFFFF"),
                        Limit.limit().count(3)
                );

        if (result == null) {
            return List.of();
        }

        return new ArrayList<>(result);
    }

    public void clear() {
        stringRedisTemplate.delete(KEY);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}