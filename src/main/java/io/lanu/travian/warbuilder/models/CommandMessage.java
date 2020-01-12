package io.lanu.travian.warbuilder.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommandMessage implements Serializable {
    private String playerId;
    private CommandsEnum command;
    private String villageName;
    private AttackRequest attackRequest;

    public CommandMessage(String playerId, CommandsEnum command, String villageName) {
        this.playerId = playerId;
        this.command = command;
        this.villageName = villageName;
        this.attackRequest = null;
    }

    public CommandMessage(String playerId, CommandsEnum command, AttackRequest attackRequest) {
        this.playerId = playerId;
        this.command = command;
        this.attackRequest = attackRequest;
    }
}
