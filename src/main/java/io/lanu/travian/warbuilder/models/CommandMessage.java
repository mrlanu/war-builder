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
    private String clientId;
    private CommandsEnum command;
    private String villageName;
    private AttackRequest attackRequest;

    public CommandMessage(String clientId, CommandsEnum command, String villageName) {
        this.clientId = clientId;
        this.command = command;
        this.villageName = villageName;
        this.attackRequest = null;
    }

    public CommandMessage(String clientId, CommandsEnum command, AttackRequest attackRequest) {
        this.clientId = clientId;
        this.command = command;
        this.attackRequest = attackRequest;
    }
}
