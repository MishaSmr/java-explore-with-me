package ru.practicum.explorewithme.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EndpointHit {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
