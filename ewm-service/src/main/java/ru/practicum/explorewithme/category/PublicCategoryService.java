package ru.practicum.explorewithme.category;

import ru.practicum.explorewithme.category.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {

    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto get(Long catId);
}
