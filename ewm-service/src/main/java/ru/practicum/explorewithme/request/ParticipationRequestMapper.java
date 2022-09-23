package ru.practicum.explorewithme.request;

import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParticipationRequestMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return new ParticipationRequestDto(
                participationRequest.getId(),
                participationRequest.getCreated().format(FORMATTER),
                participationRequest.getEvent().getId(),
                participationRequest.getRequester().getId(),
                participationRequest.getStatus()
        );
    }
}
