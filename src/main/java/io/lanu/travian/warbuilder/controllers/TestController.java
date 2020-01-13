package io.lanu.travian.warbuilder.controllers;

import io.lanu.travian.warbuilder.services.AttacksService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private AttacksService attacksService;

    public TestController(AttacksService attacksService) {
        this.attacksService = attacksService;
    }

    @GetMapping("/test")
    public void getVillagesForSpam(){
        attacksService.getVillagesForSpam();
    }
}
