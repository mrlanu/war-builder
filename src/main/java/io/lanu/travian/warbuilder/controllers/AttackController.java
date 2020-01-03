package io.lanu.travian.warbuilder.controllers;

import io.lanu.travian.warbuilder.models.AttackRequest;
import io.lanu.travian.warbuilder.services.AttacksService;
import io.lanu.travian.warbuilder.services.InformationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AttackController {

    private AttacksService attacksService;
    private InformationService informationService;

    public AttackController(AttacksService attacksService, InformationService informationService) {
        this.attacksService = attacksService;
        this.informationService = informationService;
    }

    @GetMapping("/villages")
    public Map<String, String> getAllVillages(){
        return informationService.getAllVillages();
    }

    @PostMapping("/attacks")
    public String scheduleAttack(@RequestBody List<AttackRequest> attackRequest){
        attacksService.scheduleAttack(attackRequest);
        return "Attack has been created.";
    }

    @GetMapping("/troops")
    public Integer[] getAvailableTroops(){
        return informationService.getAvailableTroops();
    }

}
