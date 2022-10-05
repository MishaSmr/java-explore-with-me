package ru.practicum.explorewithme.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.event.Event;
import ru.practicum.explorewithme.event.EventRepository;
import ru.practicum.explorewithme.event.State;
import ru.practicum.explorewithme.exceptions.ParticipationRequestException;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        eventRepository.checkEventId(eventId);
        Event event = eventRepository.getReferenceById(eventId);
        if (userId == event.getInitiator().getId()) {
            throw new ParticipationRequestException("Initiator cannot add a request to participate in their event");
        }
        if (event.getState() != State.PUBLISHED) {
            throw new ParticipationRequestException("Participation request can do only in published events");
        }
        if (event.getParticipantLimit() == event.getConfirmedRequests() && event.getParticipantLimit() != 0) {
            throw new ParticipationRequestException("Participant limit reached");
        }
        ParticipationRequest request = new ParticipationRequest(
                null,
                LocalDateTime.now(),
                event,
                userRepository.getReferenceById(userId),
                Status.PENDING
        );
        request.setEvent(event);
        request.setRequester(userRepository.getReferenceById(userId));
        if (!event.isRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        log.info("Creating request in event {}", event.getId());
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> get(Long userId) {
        log.info("Get requests for user {}", userId);
        return participationRequestRepository.findByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        ParticipationRequest request = participationRequestRepository.getReferenceById(requestId);
        if (request.getStatus() == Status.CONFIRMED) {
            Event event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
        }
        request.setStatus(Status.CANCELED);
        log.info("Canceling request in event {}", request.getEvent());
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(request));
    }
}
