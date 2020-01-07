package io.lanu.travian.warbuilder.controllers;

import io.lanu.travian.warbuilder.models.AttackRequest;
import io.lanu.travian.warbuilder.models.VillageModel;
import io.lanu.travian.warbuilder.services.AttacksService;
import io.lanu.travian.warbuilder.services.InformationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AttackController {

    private AttacksService attacksService;
    private InformationService informationService;

    public AttackController(AttacksService attacksService, InformationService informationService) {
        this.attacksService = attacksService;
        this.informationService = informationService;
    }

    @GetMapping("/villages")
    public List<VillageModel> getAllVillages(){
        return informationService.getAllVillages();
    }

    @PostMapping("/attacks")
    public String scheduleAttack(@RequestBody AttackRequest attackRequest){
        attacksService.scheduleAttack(attackRequest);
        return "Attack has been created.";
    }

    @GetMapping("/troops/{villageName}")
    public Integer[] getAvailableTroops(@PathVariable String villageName){
        return informationService.getAvailableTroops(villageName);
    }

}
