package ru.practicum.explorewithme;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    List<EndpointHit> findByUriAndTimestampAfterAndTimestampBefore(String uri, LocalDateTime start, LocalDateTime end);

}
