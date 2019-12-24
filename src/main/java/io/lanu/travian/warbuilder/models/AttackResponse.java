package io.lanu.travian.warbuilder.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttackResponse {
    /*private Integer waves;
    private Integer timeToAttack;*/

    public AttackResponse(LocalDateTime localTime) {
        this.localTime = localTime;
    }

    private LocalDateTime localTime;
}
