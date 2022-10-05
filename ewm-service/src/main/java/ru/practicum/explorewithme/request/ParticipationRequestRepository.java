package ru.practicum.explorewithme.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("select p from ParticipationRequest p" +
            " where p.requester.id = ?1")
    List<ParticipationRequest> findByRequesterId(Long userId);

    @Query("select p from ParticipationRequest p" +
            " where p.event.id = ?1")
    List<ParticipationRequest> findByEventId(Long eventId);

    @Query("select p from ParticipationRequest p" +
            " where p.event.id = ?1" +
            " and p.status = ?2")
    List<ParticipationRequest> findByEventIdAndStatus(Long eventId, Status status);
}
