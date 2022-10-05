package ru.practicum.explorewithme;

import java.util.List;

public interface StatsService {

    EndpointHitDto create(EndpointHitDto endpointHitDto);

    List<ViewStats> get(String start, String end, List<String> uris, boolean unique);
}
