package io.lanu.travian.warbuilder.amqp;

import io.lanu.travian.warbuilder.models.Order;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RabbitListener(queues = "#{autoDeleteQueue1.name}")
public class WarReceiver {

    @RabbitHandler
    public void receive(Order order) {
        System.out.println(" [x] Received '" + order.toString());
    }
}
