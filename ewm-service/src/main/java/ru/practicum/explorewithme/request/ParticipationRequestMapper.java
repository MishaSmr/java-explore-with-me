package ru.practicum.explorewithme.request;

import ru.practicum.explorewithme.EWMDateTimeFormatter;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;

import java.time.format.DateTimeFormatter;

public class ParticipationRequestMapper {

    private static final DateTimeFormatter formatter = EWMDateTimeFormatter.FORMATTER;

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return new ParticipationRequestDto(
                participationRequest.getId(),
                participationRequest.getCreated().format(formatter),
                participationRequest.getEvent().getId(),
                participationRequest.getRequester().getId(),
                participationRequest.getStatus()
        );
    }
}
