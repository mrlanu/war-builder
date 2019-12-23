package io.lanu.travian.warbuilder.models;

import lombok.Data;

import java.time.LocalTime;

@Data
public class AttackResponse {
    /*private Integer waves;
    private Integer timeToAttack;*/

    public AttackResponse(LocalTime localTime) {
        this.localTime = localTime;
    }

    private LocalTime localTime;
}
