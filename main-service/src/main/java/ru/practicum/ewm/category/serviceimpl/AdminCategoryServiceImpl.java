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
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.DataConflictException;
import ru.practicum.ewm.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

import static ru.practicum.ewm.utils.ExceptionMessages.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

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

        Category category = checkCategoryId(catId);
        if (!category.getName().equals(categoryRequest.getName()))
            checkCategoryName(categoryRequest.getName());
        category.setName(categoryRequest.getName());
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long catId) {
        checkCategoryId(catId);
        checkCategoryEvents(catId);
        categoryRepository.deleteById(catId);
    }

    private void checkCategoryName(String name) {
        Optional<Category> categoryOpt = categoryRepository.findByName(name);
        if (categoryOpt.isPresent())
            throw new DataConflictException(CATEGORY_WRONG_NAME);
    }

    private Category checkCategoryId(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NO_ID));
    }

    private void checkCategoryEvents(Long catId) {
        List<Event> events = eventRepository.findAllByCategory_CatId(catId);
        if (events != null && !events.isEmpty()) {
            throw new DataConflictException(CATEGORY_CONSIST_EVENTS);
        }
    }
}
