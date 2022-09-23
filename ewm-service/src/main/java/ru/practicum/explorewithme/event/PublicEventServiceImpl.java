package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.client.EndpointHit;
import ru.practicum.explorewithme.client.StatsClient;
import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.exceptions.EventNotPublishedException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;

    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categoryIds,
                                         Boolean paid,
                                         String rangeStart,
                                         String rangeEnd,
                                         Boolean onlyAvailable,
                                         SortEnum sort,
                                         Integer from,
                                         Integer size,
                                         String ip,
                                         String path) {
        LocalDateTime start;
        if (rangeStart == null) {
            start = LocalDateTime.now();
        } else {
            start = LocalDateTime.parse(rangeStart, FORMATTER);
        }
        LocalDateTime end = LocalDateTime.parse(rangeEnd, FORMATTER);
        int page = from / size;
        Sort s = Sort.by(Sort.Direction.DESC, "eventDate", "id");
        Pageable pageable = PageRequest.of(page, size, s);
        Page<Event> events;
        if (onlyAvailable) {
            events = eventRepository.findWithPublicParametersOnlyAvailable(State.PUBLISHED,
                    text.toLowerCase(),
                    categoryIds,
                    paid,
                    start,
                    end,
                    pageable);
        } else {
            events = eventRepository.findWithPublicParameters(State.PUBLISHED,
                    text.toLowerCase(),
                    categoryIds,
                    paid,
                    start,
                    end,
                    pageable);
        }
        List<EventShortDto> result = events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        result.forEach(e -> e.setViews(statsClient.getViews(e.getId())));
        if (sort == SortEnum.VIEWS) {
            result.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        }
        statsClient.createEndpointHit(new EndpointHit(
                "Explore With Me",
                path,
                ip,
                LocalDateTime.now().format(FORMATTER)
        ));
        log.info("Get events with public search parameters");
        return result;
    }

    @Override
    public EventFullDto get(Long id, String ip, String path) {
        eventRepository.checkEventId(id);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.getReferenceById(id));
        if (eventFullDto.getState() != State.PUBLISHED) {
            throw new EventNotPublishedException("Only published events can be get");
        }
        eventFullDto.setViews(statsClient.getViews(id));
        statsClient.createEndpointHit(new EndpointHit(
                "Explore With Me",
                path,
                ip,
                LocalDateTime.now().format(FORMATTER)
        ));
        log.info("Get event with id {}", id);
        return eventFullDto;
    }
}
