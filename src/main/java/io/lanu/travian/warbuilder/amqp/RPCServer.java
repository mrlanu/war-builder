package io.lanu.travian.warbuilder.amqp;

import io.lanu.travian.warbuilder.models.AboutVillageModel;
import io.lanu.travian.warbuilder.models.CommandMessage;
import io.lanu.travian.warbuilder.models.VillageModel;
import io.lanu.travian.warbuilder.services.InformationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RPCServer {

    private InformationService informationService;

    public RPCServer(InformationService informationService) {
        this.informationService = informationService;
    }

    @RabbitListener(queues = "rpc.requests")
    public AboutVillageModel allVillages(CommandMessage commandMessage) {
        System.out.println(" [x] Received request for update...");
        List<VillageModel> allVillages = informationService.getAllVillages();
        Integer[] availableTroops = informationService.getAvailableTroops(commandMessage.getVillageName());
        System.out.println(" [x] Update Returned.");
        return new AboutVillageModel(commandMessage.getVillageName(), availableTroops, allVillages);
    }
}
