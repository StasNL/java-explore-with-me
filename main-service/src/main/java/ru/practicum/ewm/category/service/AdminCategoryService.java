package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryRequest;
import ru.practicum.ewm.category.dto.NewCategoryRequest;
import ru.practicum.ewm.category.model.Category;

public interface AdminCategoryService {

    Category createCategory(NewCategoryRequest category);

    Category editCategory(Long catId, CategoryRequest category);

    void deleteCategory(Long catId);
}
