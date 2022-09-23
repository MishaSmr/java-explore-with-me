package ru.practicum.explorewithme.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;


@Data
@AllArgsConstructor
public class Location {
    private float lat;
    private float lon;
}
