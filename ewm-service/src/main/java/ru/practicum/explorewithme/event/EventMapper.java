package ru.practicum.explorewithme.event;

import ru.practicum.explorewithme.EWMDateTimeFormatter;
import ru.practicum.explorewithme.category.CategoryMapper;
import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.event.dto.NewEventDto;
import ru.practicum.explorewithme.user.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

public class EventMapper {

    private static final DateTimeFormatter formatter = EWMDateTimeFormatter.FORMATTER;

    public static Event toEvent(NewEventDto newEventDto) {
        return new Event(
                null,
                newEventDto.getTitle(),
                newEventDto.getAnnotation(),
                null,
                newEventDto.getDescription(),
                LocalDateTime.parse(newEventDto.getEventDate(), formatter),
                LocalDateTime.now(),
                null,
                0,
                newEventDto.isPaid(),
                newEventDto.getParticipantLimit(),
                null,
                newEventDto.isRequestModeration(),
                State.PENDING,
                newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon(),
                Collections.emptyList()
        );
    }

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getDescription(),
                event.getEventDate().format(formatter),
                event.getCreatedOn().format(formatter),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getConfirmedRequests(),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn() == null ? null : event.getPublishedOn().format(formatter),
                event.isRequestModeration(),
                event.getState(),
                0,
                new Location(event.getLocationLat(), event.getLocationLon())
        );
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getEventDate().format(formatter),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getConfirmedRequests(),
                event.isPaid(),
                0
        );
    }

    public static EventShortDto toEventShortDto(Map.Entry<Event, Long> entry) {
        return new EventShortDto(
                entry.getKey().getId(),
                entry.getKey().getTitle(),
                entry.getKey().getAnnotation(),
                CategoryMapper.toCategoryDto(entry.getKey().getCategory()),
                entry.getKey().getEventDate().format(formatter),
                UserMapper.toUserShortDto(entry.getKey().getInitiator()),
                entry.getKey().getConfirmedRequests(),
                entry.getKey().isPaid(),
                entry.getValue()
        );
    }
}
