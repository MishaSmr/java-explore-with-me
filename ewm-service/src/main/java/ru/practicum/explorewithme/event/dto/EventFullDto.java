package ru.practicum.explorewithme.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.explorewithme.category.dto.CategoryDto;
import ru.practicum.explorewithme.event.Location;
import ru.practicum.explorewithme.event.State;
import ru.practicum.explorewithme.user.dto.UserShortDto;

@Data
@AllArgsConstructor
public class EventFullDto {
    private long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private String description;
    private String eventDate;
    private String createdOn;
    private UserShortDto initiator;
    private int confirmedRequests;
    private boolean paid;
    private int participantLimit;
    private String publishedOn;
    private boolean requestModeration;
    private State state;
    private long views;
    private Location location;
}
