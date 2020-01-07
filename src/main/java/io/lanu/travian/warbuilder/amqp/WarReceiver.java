package io.lanu.travian.warbuilder.amqp;

import io.lanu.travian.warbuilder.models.CommandMessage;
import io.lanu.travian.warbuilder.services.AttacksService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "#{autoDeleteQueue.name}")
public class WarReceiver {

    private AttacksService attacksService;

    public WarReceiver(AttacksService attacksService) {
        this.attacksService = attacksService;
    }

    @RabbitHandler
    public void receive(CommandMessage commandMessage) {
        System.out.println(" [x] Received command for attack.");
        attacksService.scheduleAttack(commandMessage.getAttackRequest());
    }
}
