package ru.practicum.explorewithme.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class NewCategoryDto {
    @NotNull
    @NotEmpty
    private String name;

    public NewCategoryDto() {
    }
}
