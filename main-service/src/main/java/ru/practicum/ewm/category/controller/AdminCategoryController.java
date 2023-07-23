package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.dto.CategoryRequest;
import ru.practicum.ewm.category.dto.CategoryResponse;
import ru.practicum.ewm.category.dto.NewCategoryRequest;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.AdminCategoryService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RestController
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final AdminCategoryService categoryService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CategoryResponse createCategory(@RequestBody @Valid NewCategoryRequest newCategory) {

        Category category = categoryService.createCategory(newCategory);
        return CategoryMapper.categoryToCategoryResponse(category);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CategoryResponse editCategory(@PathVariable @NotNull @Positive Long catId,
                                         @RequestBody @Valid CategoryRequest categoryRequest) {

        Category category = categoryService.editCategory(catId, categoryRequest);
        return CategoryMapper.categoryToCategoryResponse(category);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @NotNull @Positive Long catId) {
        categoryService.deleteCategory(catId);
    }
}