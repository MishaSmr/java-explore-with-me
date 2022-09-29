package ru.practicum.explorewithme.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.exceptions.EventNotFoundException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e from Event e" +
            " where e.initiator.id = ?1")
    Page<Event> findByInitiator(Long userId, Pageable pageable);

    @Query("select e from Event e" +
            " where e.initiator.id in ?1" +
            " and e.state in ?2" +
            " and e.category.id in ?3" +
            " and e.eventDate >= ?4" +
            " and e.eventDate <= ?5")
    Page<Event> findWithAdminParameters(Collection<Long> userIds,
                                        Collection<State> states,
                                        Collection<Long> categoryIds,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Pageable pageable);

    @Query("select e from Event e" +
            " where e.state = ?1" +
            " and upper(e.annotation) like upper(concat('%', ?2, '%'))" +
            " or upper(e.description) like upper(concat('%', ?2, '%'))" +
            " and e.category.id in ?3" +
            " and e.paid = ?4" +
            " and e.eventDate >= ?5" +
            " and e.eventDate <= ?6")
    Page<Event> findWithPublicParameters(State state,
                                         String text,
                                         List<Long> categoryIds,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Pageable pageable);

    @Query("select e from Event e" +
            " where e.state = ?1" +
            " and e.annotation = ?2" +
            " or e.description = ?2" +
            " and e.category.id in ?3" +
            " and e.paid = ?4" +
            " and e.eventDate >= ?5" +
            " and e.eventDate <= ?6" +
            " and e.participantLimit > e.confirmedRequests")
    Page<Event> findWithPublicParametersOnlyAvailable(State state,
                                                      String text,
                                                      List<Long> categoryIds,
                                                      Boolean paid,
                                                      LocalDateTime rangeStart,
                                                      LocalDateTime rangeEnd,
                                                      Pageable pageable);

    @Query("select e from Event e" +
            " left outer join Subscription s on e.initiator = s.user" +
            " where s.follower.id = ?1" +
            " and e.eventDate > current_timestamp" +
            " and e.state = 'PUBLISHED'")
    Page<Event> findEventsByInitiatorForFollower(Long userId,
                                                 Pageable pageable);

    @Query("select e from Event e" +
            " left outer join ParticipationRequest p on e = p.event" +
            " left outer join Subscription s on p.requester = s.user" +
            " where s.follower.id = ?1" +
            " and s.approved = true" +
            " and e.eventDate > current_timestamp" +
            " and e.state = 'PUBLISHED'" +
            " and p.status = 'CONFIRMED'")
    Page<Event> findEventsByParticipantForFollower(Long userId,
                                                   Pageable pageable);

    default void checkEventId(Long eventId) {
        try {
            Event event = getReferenceById(eventId);
            EventMapper.toEventShortDto(event);
        } catch (EntityNotFoundException ex) {
            throw new EventNotFoundException(String.format("Event with id=%d was not found.", eventId));
        }
    }
}
