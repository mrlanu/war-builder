package io.lanu.travian.warbuilder;

import io.lanu.travian.warbuilder.entities.WaveEntity;
import io.lanu.travian.warbuilder.repositories.WaveRepository;
import io.lanu.travian.warbuilder.services.AttacksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MainClass implements CommandLineRunner {

    private AttacksService attacksService;
    private WaveRepository waveRepository;

    @Autowired
    public MainClass(AttacksService attacksService, WaveRepository waveRepository) {
        this.attacksService = attacksService;
        this.waveRepository = waveRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(String.format("Hello - %s", attacksService.login()));
    }
}
