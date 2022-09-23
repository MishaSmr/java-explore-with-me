package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.category.CategoryRepository;
import ru.practicum.explorewithme.client.StatsClient;
import ru.practicum.explorewithme.event.dto.AdminUpdateEventRequest;
import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.exceptions.EventPublicationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

    private final StatsClient statsClient;

    @Override
    public List<EventFullDto> getEvents(List<Long> userIds,
                                        List<State> states,
                                        List<Long> categoryIds,
                                        String rangeStart,
                                        String rangeEnd,
                                        Integer from,
                                        Integer size) {
        LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.ASC, "eventDate");
        Pageable pageable = PageRequest.of(page, size, sort);
        List<EventFullDto> result = eventRepository.findWithAdminParameters(userIds,
                        states,
                        categoryIds,
                        start,
                        end,
                        pageable)
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
        result.forEach(e -> e.setViews(statsClient.getViews(e.getId())));
        log.info("Get events with admin search parameters");
        return result;
    }

    @Transactional
    @Override
    public EventFullDto editEvent(Long eventId, AdminUpdateEventRequest updateEventRequest) {
        Event event = eventRepository.getReferenceById(eventId);
        if (updateEventRequest.getTitle() != null) event.setTitle(updateEventRequest.getTitle());
        if (updateEventRequest.getAnnotation() != null) event.setAnnotation(updateEventRequest.getAnnotation());
        if (updateEventRequest.getCategoryId() != null) {
            event.setCategory(categoryRepository.getReferenceById(updateEventRequest.getCategoryId()));
        }
        if (updateEventRequest.getDescription() != null) event.setDescription(updateEventRequest.getDescription());
        if (updateEventRequest.getEventDate() != null)
            event.setEventDate(LocalDateTime.parse(updateEventRequest.getEventDate(), formatter));
        if (updateEventRequest.getPaid() != null) event.setPaid(updateEventRequest.getPaid());
        if (updateEventRequest.getParticipantLimit() != null)
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        if (updateEventRequest.getLocation() != null) {
            event.setLocationLat(updateEventRequest.getLocation().getLat());
            event.setLocationLon(updateEventRequest.getLocation().getLon());
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setViews(statsClient.getViews(eventFullDto.getId()));
        log.info("Edit event {} by admin", event.getId());
        return eventFullDto;
    }

    @Transactional
    @Override
    public EventFullDto publishEvent(Long eventId) {
        Event event = eventRepository.getReferenceById(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new EventPublicationException("Event date should be no earlier than an hour from the publication time");
        }
        if (event.getState() != State.PENDING) {
            throw new EventPublicationException("Only pending events can be published");
        }
        event.setState(State.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setViews(statsClient.getViews(eventFullDto.getId()));
        log.info("Publish event {} by admin", event.getId());
        return eventFullDto;
    }

    @Transactional
    @Override
    public EventFullDto rejectEvent(Long eventId) {
        Event event = eventRepository.getReferenceById(eventId);
        if (event.getState() == State.PUBLISHED) {
            throw new EventPublicationException("Only pending events can be rejected");
        }
        event.setState(State.CANCELED);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setViews(statsClient.getViews(eventFullDto.getId()));
        log.info("Publish event {} by admin", event.getId());
        return eventFullDto;
    }
}
