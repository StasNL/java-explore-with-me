package ru.practicum.ewm.category.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.model.Category;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryMapper {

    public static CategoryResponse categoryToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getCatId())
                .name(category.getName())
                .build();
    }
}
