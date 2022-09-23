package ru.practicum.explorewithme.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsClient extends BaseClient {

    // private static final String API_PREFIX = "/hit";

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getStats(String uri) {
        Map<String, Object> parameters = Map.of(
                "uris", uri
        );
        return get("/stats/?uris={uris}", parameters);
    }

    public ResponseEntity<Object> createEndpointHit(EndpointHit endpointHit) {
        return post("/hit", endpointHit);
    }

    public long getViews(Long eventId) {
        ResponseEntity<Object> responseEntity = getStats("/events/" + eventId);
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
}
