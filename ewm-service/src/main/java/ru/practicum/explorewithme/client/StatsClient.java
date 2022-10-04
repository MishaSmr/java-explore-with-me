package ru.practicum.explorewithme.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getStats(List<String> uris) {
        StringBuilder sb = new StringBuilder();
        for (String uri : uris) {
          sb.append("&uris=").append(uri);
        }
        return get("/stats/?" + sb);
    }

    public ResponseEntity<Object> createEndpointHit(EndpointHit endpointHit) {
        return post("/hit", endpointHit);
    }

    public long getViews(Long eventId) {
        List<String> uris = List.of("/events/" + eventId);
        ResponseEntity<Object> responseEntity = getStats(uris);
        List<Object> response = (List<Object>) responseEntity.getBody();
        if (response == null) {
            return 0;
        }
        ObjectMapper mapper = new ObjectMapper();
        List<ViewStats> result = response.stream()
                .map(object -> mapper.convertValue(object, ViewStats.class))
                .collect(Collectors.toList());
        if (result.isEmpty()) return 0;
        return result.get(0).getHits();
    }

    public List<Long> getViewsForList(List<Long> eventIds) {
        List<String> uris = new ArrayList<>();
        for (Long eventId : eventIds) {
           uris.add("/events/" + eventId);
        }
        ResponseEntity<Object> responseEntity = getStats(uris);
        List<Object> response = (List<Object>) responseEntity.getBody();
        if (response == null) {
            return Collections.emptyList();
        }
        ObjectMapper mapper = new ObjectMapper();
        return response.stream()
                .map(object -> mapper.convertValue(object, ViewStats.class).getHits())
                .collect(Collectors.toList());
    }
}
