package ru.practicum.explorewithme.exceptions;

import java.util.List;

public class ValidationException extends RuntimeException {
    public List<String> getMessages() {
        return messages;
    }

    List<String> messages;

    public ValidationException(List<String> messages) {
        this.messages = messages;
    }
}