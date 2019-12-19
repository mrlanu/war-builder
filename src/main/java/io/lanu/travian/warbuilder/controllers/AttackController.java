package io.lanu.travian.warbuilder.controllers;

import io.lanu.travian.warbuilder.models.AttackRequest;
import io.lanu.travian.warbuilder.services.AttacksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/attacks")
public class AttackController {

    private AttacksService attacksService;

    @Autowired
    public AttackController(AttacksService attacksService) {
        this.attacksService = attacksService;
    }

    @PostMapping
    public void createAttack(@RequestBody List<AttackRequest> attackRequest){
        attacksService.createAttack(attackRequest);
    }
}
