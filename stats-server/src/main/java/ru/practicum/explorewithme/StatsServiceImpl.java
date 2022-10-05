package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository repository;

    @Transactional
    @Override
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHitMapper.toEndpointHit(endpointHitDto);
        log.info("Creating endpoint hit {}", endpointHitDto.getUri());
        return EndpointHitMapper.endpointHitDto(repository.save(endpointHit));
    }

    @Override
    public List<ViewStats> get(String start, String end, List<String> uris, boolean unique) {
        log.info("Get stats");
        if (uris == null) {
            return Collections.emptyList();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<ViewStats> result = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.parse(decode(start), formatter);
        LocalDateTime endTime = LocalDateTime.parse(decode(end), formatter);
        for (String uri : uris) {
            List<EndpointHit> forUriResult = repository
                    .findByUriAndTimestampAfterAndTimestampBefore(uri, startTime, endTime);
            if (!forUriResult.isEmpty()) {
                ViewStats viewStats = ViewStats.builder()
                        .app(forUriResult.get(0).getApp())
                        .uri(uri)
                        .hits(forUriResult.size())
                        .build();
                if (unique) {
                    long uniqueHits = forUriResult.stream()
                            .map(EndpointHit::getIp)
                            .distinct()
                            .count();
                    viewStats.setHits(uniqueHits);
                }
                result.add(viewStats);
            }
        }
        return result;
    }

    private String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
