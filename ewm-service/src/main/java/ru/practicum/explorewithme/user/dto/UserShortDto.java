package ru.practicum.explorewithme.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class UserShortDto {
    private long id;
    private String name;
}
