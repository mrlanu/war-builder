package io.lanu.travian.warbuilder.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttackRequest {
    private Integer x;
    private Integer y;
    private Integer kindAttack;
    private Integer[] troops;
    private LocalDateTime time; // format HH:MM:SS
}
