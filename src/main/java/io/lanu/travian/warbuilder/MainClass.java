package io.lanu.travian.warbuilder;

import io.lanu.travian.warbuilder.services.AttacksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MainClass implements CommandLineRunner {

    private AttacksService attacksService;

    @Autowired
    public MainClass(AttacksService attacksService) {
        this.attacksService = attacksService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(String.format("Hello - %s", attacksService.login()));
    }
}
