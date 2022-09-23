package ru.practicum.explorewithme.category;

import ru.practicum.explorewithme.category.dto.CategoryDto;
import ru.practicum.explorewithme.category.dto.NewCategoryDto;

public interface AdminCategoryService {

    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(Long catId);
}
