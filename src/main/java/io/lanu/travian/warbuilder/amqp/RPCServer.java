package io.lanu.travian.warbuilder.amqp;

import io.lanu.travian.warbuilder.models.AboutVillageModel;
import io.lanu.travian.warbuilder.models.CommandMessage;
import io.lanu.travian.warbuilder.models.CommandsEnum;
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

        List<VillageModel> allVillages = null;
        Integer[] availableTroops = null;

        System.out.println(" [x] Received request for information...");

        if (commandMessage.getCommand().equals(CommandsEnum.ALL_VILLAGES)){
            allVillages = informationService.getAllVillages();
        }else if (commandMessage.getCommand().equals(CommandsEnum.TROOPS)){
            availableTroops = informationService.getAvailableTroops(commandMessage.getVillageName());
        }

        System.out.println(" [x] Update Returned.");

        return new AboutVillageModel(commandMessage.getVillageName(), availableTroops, allVillages);
    }
}
