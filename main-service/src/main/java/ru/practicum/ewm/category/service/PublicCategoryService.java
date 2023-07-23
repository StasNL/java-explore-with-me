package ru.practicum.ewm.category.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.model.Category;

import java.util.List;

@Service
public interface PublicCategoryService {

    Category getCategoryById(Long catId);

    List<Category> getAllCategories(Integer from, Integer size);
}
