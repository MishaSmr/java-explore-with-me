package ru.practicum.explorewithme.event;

import ru.practicum.explorewithme.event.dto.AdminUpdateEventRequest;
import ru.practicum.explorewithme.event.dto.EventFullDto;

import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getEvents(List<Long> userIds,
                                 List<State> states,
                                 List<Long> categoryIds,
                                 String rangeStart,
                                 String rangeEnd,
                                 Integer from,
                                 Integer size);

    EventFullDto editEvent(Long eventId, AdminUpdateEventRequest updateEventRequest);

    EventFullDto publishEvent(Long eventId);

    EventFullDto rejectEvent(Long eventId);
}
