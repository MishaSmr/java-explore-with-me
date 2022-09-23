package ru.practicum.explorewithme.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.category.dto.CategoryDto;
import ru.practicum.explorewithme.category.dto.NewCategoryDto;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);
        log.info("Creating category {}", category.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        Category category = categoryRepository.getReferenceById(categoryDto.getId());
        if (categoryDto.getName() != null) category.setName(categoryDto.getName());
        log.info("Update category, categoryId={}", category.getId());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    @Override
    public void delete(Long catId) {
        log.info("Delete category, categoryId={}", catId);
        categoryRepository.deleteById(catId);
    }
}
