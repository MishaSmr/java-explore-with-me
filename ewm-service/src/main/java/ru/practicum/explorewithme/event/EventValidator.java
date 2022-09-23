package ru.practicum.explorewithme.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class EventValidator {

    public static void validateEvent(Event event) throws ValidationException {
        List<String> messages = new ArrayList<>();
        int n = 0;
        if (event.getTitle().isEmpty()) {
            log.warn("Field: title.");
            messages.add("Field: title. Error: must not be blank. Value: " + event.getTitle());
            n++;
        }
        if (event.getAnnotation().isEmpty()) {
            log.warn("Field: annotation.");
            messages.add("Field: annotation. Error: must not be blank. Value: " + event.getAnnotation());
            n++;
        }
        if (event.getParticipantLimit() < 0) {
            log.warn("Field: participantLimit.");
            messages.add("Field: participantLimit. Error: must be greater than or equal to 0. " +
                    "Value: " + event.getParticipantLimit());
            n++;
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Field: eventDate");
            messages.add("Field: eventDate. Error: must be a date no earlier than 2 hours from the current moment . " +
                    "Value: " + event.getEventDate());
            n++;
        }
        if (n > 0) {
            throw new ValidationException(messages);
        }
    }
}
