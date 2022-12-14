package ru.practicum.explorewithme;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndpointHitDto {
    private long id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
