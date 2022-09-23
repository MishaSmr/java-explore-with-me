package ru.practicum.explorewithme.event;

import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.EventShortDto;

import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getEvents(String text,
                                  List<Long> categoryIds,
                                  Boolean paid,
                                  String rangeStart,
                                  String rangeEnd,
                                  Boolean onlyAvailable,
                                  SortEnum sort,
                                  Integer from,
                                  Integer size,
                                  String ip,
                                  String path);

    EventFullDto get(Long id, String ip, String path);
}
