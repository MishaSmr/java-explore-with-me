package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.EWMDateTimeFormatter;
import ru.practicum.explorewithme.category.CategoryRepository;
import ru.practicum.explorewithme.client.StatsClient;
import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.event.dto.NewEventDto;
import ru.practicum.explorewithme.event.dto.UpdateEventRequest;
import ru.practicum.explorewithme.exceptions.EventUpdateException;
import ru.practicum.explorewithme.exceptions.ParticipationRequestException;
import ru.practicum.explorewithme.request.ParticipationRequest;
import ru.practicum.explorewithme.request.ParticipationRequestMapper;
import ru.practicum.explorewithme.request.ParticipationRequestRepository;
import ru.practicum.explorewithme.request.Status;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {

    private static final DateTimeFormatter formatter = EWMDateTimeFormatter.FORMATTER;

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    private final StatsClient statsClient;

    @Transactional
    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        Event event = EventMapper.toEvent(newEventDto);
        EventValidator.validateEvent(event);
        event.setInitiator(userRepository.getReferenceById(userId));
        if (newEventDto.getCategory() != null) {
            event.setCategory(categoryRepository.getReferenceById(newEventDto.getCategory()));
        }
        log.info("Creating event {} with catId={}", event.getTitle(), newEventDto.getCategory());
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "eventDate", "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        List<EventShortDto> result = eventRepository.findByInitiator(userId, pageable).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        result.forEach(e -> e.setViews(statsClient.getViews(e.getId())));
        log.info("Get events for user {}", userId);
        return result;
    }

    @Transactional
    @Override
    public EventFullDto update(Long userId, UpdateEventRequest updateEventRequest) {
        eventRepository.checkEventId(updateEventRequest.getEventId());
        Event event = eventRepository.getReferenceById(updateEventRequest.getEventId());
        if (event.getState() == State.PUBLISHED) {
            throw new EventUpdateException("Only pending or canceled events can be changed");
        }
        if (event.getState() == State.CANCELED) event.setState(State.PENDING);
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
        EventValidator.validateEvent(event);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setViews(statsClient.getViews(eventFullDto.getId()));
        log.info("Updating event {}", event.getTitle());
        return eventFullDto;
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        eventRepository.checkEventId(eventId);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.getReferenceById(eventId));
        eventFullDto.setViews(statsClient.getViews(eventId));
        log.info("Get event {}", eventId);
        return eventFullDto;
    }

    @Transactional
    @Override
    public EventFullDto cancelEvent(Long userId, Long eventId) {
        eventRepository.checkEventId(eventId);
        Event event = eventRepository.getReferenceById(eventId);
        if (event.getState() != State.PENDING) {
            throw new EventUpdateException("Only pending events can be canceled");
        }
        event.setState(State.CANCELED);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setViews(statsClient.getViews(eventId));
        log.info("Canceling event {}", eventId);
        return eventFullDto;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsForInitiator(Long userId, Long eventId) {
        eventRepository.checkEventId(eventId);
        log.info("Get requests for event {}", eventId);
        return participationRequestRepository.findByEventId(eventId).stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) {
        eventRepository.checkEventId(eventId);
        ParticipationRequest request = participationRequestRepository.getReferenceById(reqId);
        Event event = eventRepository.getReferenceById(eventId);
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ParticipationRequestException("Confirmation is not required");
        }
        if (event.getParticipantLimit() == event.getConfirmedRequests()) {
            throw new ParticipationRequestException("Participant limit reached");
        }
        request.setStatus(Status.CONFIRMED);
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.save(event);
        if (event.getParticipantLimit() == event.getConfirmedRequests()) {
            rejectRequests(eventId);
        }
        log.info("Confirm request {} for event {}", reqId, eventId);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(request));
    }

    @Transactional
    @Override
    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId) {
        eventRepository.checkEventId(eventId);
        ParticipationRequest request = participationRequestRepository.getReferenceById(reqId);
        Event event = eventRepository.getReferenceById(eventId);
        request.setStatus(Status.REJECTED);
        event.setConfirmedRequests(event.getConfirmedRequests() - 1);
        eventRepository.save(event);
        log.info("Reject request {} for event {}", reqId, eventId);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(request));
    }

    private void rejectRequests(Long eventId) {
        List<ParticipationRequest> requests = participationRequestRepository
                .findByEventIdAndStatus(eventId, Status.PENDING);
        for (ParticipationRequest r : requests) {
            r.setStatus(Status.REJECTED);
            participationRequestRepository.save(r);
        }
    }
}
