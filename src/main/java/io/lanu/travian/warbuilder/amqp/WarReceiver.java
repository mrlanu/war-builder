package io.lanu.travian.warbuilder.amqp;

import io.lanu.travian.warbuilder.models.CommandMessage;
import io.lanu.travian.warbuilder.models.CommandsEnum;
import io.lanu.travian.warbuilder.services.AttacksService;
import io.lanu.travian.warbuilder.services.Player;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "#{autoDeleteQueue.name}")
public class WarReceiver {

    private AttacksService attacksService;
    private Player player;

    public WarReceiver(AttacksService attacksService, Player player) {
        this.attacksService = attacksService;
        this.player = player;
    }

    @RabbitHandler
    public void receive(CommandMessage commandMessage) {
        if (player.getPlayerId().equals(commandMessage.getPlayerId())) {
            System.out.println(" [x] Received command for attack.");
            if (commandMessage.getCommand().equals(CommandsEnum.ATTACK)) {
                attacksService.scheduleAttack(commandMessage.getAttackRequest());
            } else if (commandMessage.getCommand().equals(CommandsEnum.SPAM)) {
                attacksService.sendSpam(commandMessage.getAttackRequest());
            }
        }
    }
}
