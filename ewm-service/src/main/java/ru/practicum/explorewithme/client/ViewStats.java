package ru.practicum.explorewithme.client;

import lombok.Data;

@Data
public class ViewStats {
    private String app;
    private String uri;
    private long hits;
}
