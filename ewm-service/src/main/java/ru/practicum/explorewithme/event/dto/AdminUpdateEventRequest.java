package ru.practicum.explorewithme.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.explorewithme.event.Location;


@Data
@AllArgsConstructor
public class AdminUpdateEventRequest {
    private String title;
    private String annotation;
    private Long categoryId;
    private String description;
    private String eventDate;
    private Boolean paid;
    private Integer participantLimit;
    private boolean requestModeration;
    private Location location;
}
