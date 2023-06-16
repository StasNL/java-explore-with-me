package ru.practicum.ewm.category.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryRequest;
import ru.practicum.ewm.category.dto.NewCategoryRequest;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.category.service.AdminCategoryService;
import ru.practicum.ewm.exceptions.BadDBRequestException;
import ru.practicum.ewm.exceptions.BadRequestException;

import java.util.Optional;

import static ru.practicum.ewm.utils.ExceptionMessages.CATEGORY_NO_ID;
import static ru.practicum.ewm.utils.ExceptionMessages.CATEGORY_WRONG_NAME;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(NewCategoryRequest newCategory) {

        checkCategoryName(newCategory.getName());

        Category category = Category.builder()
                .name(newCategory.getName())
                .build();

        return categoryRepository.save(category);
    }

    @Override
    public Category editCategory(Long catId, CategoryRequest categoryRequest) {
        checkCategoryName(categoryRequest.getName());

        Category category = checkCategoryId(catId);
        category.setName(categoryRequest.getName());

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long catId) {
        checkCategoryId(catId);
        categoryRepository.deleteById(catId);
    }

    private void checkCategoryName(String name) {
        Optional<Category> categoryOpt = categoryRepository.findByName(name);
        if (categoryOpt.isPresent())
            throw new BadRequestException(CATEGORY_WRONG_NAME);
    }

    private Category checkCategoryId(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BadDBRequestException(CATEGORY_NO_ID));
    }
}
