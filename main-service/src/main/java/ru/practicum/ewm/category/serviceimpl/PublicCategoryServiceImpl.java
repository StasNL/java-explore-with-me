package ru.practicum.ewm.category.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.category.service.PublicCategoryService;
import ru.practicum.ewm.exceptions.BadDBRequestException;

import java.util.List;

import static ru.practicum.ewm.utils.ExceptionMessages.CATEGORY_NO_ID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCategoryServiceImpl implements PublicCategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category getCategoryById(Long catId) {

        return categoryRepository.findById(catId)
                .orElseThrow(() -> new BadDBRequestException(CATEGORY_NO_ID));
    }

    @Override
    public List<Category> getAllCategories(Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from/size, size);

        return categoryRepository.findAll(pageable).toList();
    }
}
