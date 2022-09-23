package ru.practicum.explorewithme;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_hits")
@Data
@AllArgsConstructor
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;

    public EndpointHit() {

    }
}
