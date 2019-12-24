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
    public String scheduleAttack(@RequestBody List<AttackRequest> attackRequest){
        attacksService.scheduleAttack(attackRequest);
        return "Attack has been created.";
    }

    /*@GetMapping
    public AttackResponse getDate(){
        AttackResponse attackResponse = new AttackResponse(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return attackResponse;
    }*/

    /*@GetMapping("/{attackId}")
    public String confirmAttack(@PathVariable("attackId") String attackId){
        attacksService.confirmAttack(attackId);
        return "Attack has been sent.";
    }*/
}
