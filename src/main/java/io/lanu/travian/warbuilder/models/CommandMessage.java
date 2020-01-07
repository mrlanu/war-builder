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
    private CommandsEnum command;
    private String villageName;
    private AttackRequest attackRequest;

    public CommandMessage(CommandsEnum command, String villageName) {
        this.command = command;
        this.villageName = villageName;
        this.attackRequest = null;
    }

    public CommandMessage(CommandsEnum command, AttackRequest attackRequest) {
        this.command = command;
        this.attackRequest = attackRequest;
    }
}
