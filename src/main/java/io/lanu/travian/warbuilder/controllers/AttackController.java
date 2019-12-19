package io.lanu.travian.warbuilder.controllers;

import io.lanu.travian.warbuilder.models.AttackRequest;
import io.lanu.travian.warbuilder.services.AttacksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public String createAttack(@RequestBody List<AttackRequest> attackRequest){
        attacksService.createAttack(attackRequest);
        return "Attack has been created.";
    }

    @GetMapping("/{attackId}")
    public String confirmAttack(@PathVariable("attackId") String attackId){
        attacksService.confirmAttack(attackId);
        return "Attack has been sent.";
    }
}
