package ru.practicum.explorewithme.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UpdateEventRequest {
    private long eventId;
    private String title;
    private String annotation;
    private Long categoryId;
    private String description;
    private String eventDate;
    private Boolean paid;
    private Integer participantLimit;
}
