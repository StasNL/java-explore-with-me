package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.dto.CategoryResponse;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.PublicCategoryService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.utils.Constants.PAGINATION_FROM;
import static ru.practicum.ewm.utils.Constants.PAGINATION_SIZE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {

    private final PublicCategoryService categoryService;

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public CategoryResponse getCategoryById(@NotNull @PathVariable(value = "id") Long catId) {

        Category category = categoryService.getCategoryById(catId);
        return CategoryMapper.categoryToCategoryResponse(category);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<CategoryResponse> getAllCategories(
            @RequestParam(required = false, defaultValue = PAGINATION_FROM) @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = PAGINATION_SIZE) @Positive Integer size) {

        return categoryService.getAllCategories(from, size).stream()
                .map(CategoryMapper::categoryToCategoryResponse)
                .collect(Collectors.toList());
    }
}