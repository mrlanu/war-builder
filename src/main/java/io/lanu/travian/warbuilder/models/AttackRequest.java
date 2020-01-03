package io.lanu.travian.warbuilder.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttackRequest {
    private String attackId;
    private String attackingVillage;
    private Integer x;
    private Integer y;
    private Integer kindAttack;
    private Integer[] troops;
    private Integer firstTarget;
    private Integer secondTarget;
    private LocalDateTime time; // format HH:MM:SS
}
