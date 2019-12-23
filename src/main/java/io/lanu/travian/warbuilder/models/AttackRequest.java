package io.lanu.travian.warbuilder.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttackRequest {
    private Integer x;
    private Integer y;
    private Integer troops;
    private LocalTime time; // format HH:MM:SS
}
