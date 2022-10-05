package ru.practicum.explorewithme.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.explorewithme.event.Location;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class NewEventDto {
    @NotNull
    private String title;
    @NotNull
    private String annotation;
    private Long category;
    @NotNull
    private String description;
    @NotNull
    private String eventDate;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private Location location;
}
