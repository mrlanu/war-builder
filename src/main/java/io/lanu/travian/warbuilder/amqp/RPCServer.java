package io.lanu.travian.warbuilder.amqp;

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
    public List<VillageModel> allVillages() {
        System.out.println(" [x] Received request for all villages...");
        List<VillageModel> result = informationService.getAllVillages();
        System.out.println(" [x] All villages Returned.");
        return result;
    }
}
