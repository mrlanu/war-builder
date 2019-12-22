package io.lanu.travian.warbuilder;

import io.lanu.travian.warbuilder.services.AttacksService;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.convert.DurationFormat;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;

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
        LocalTime now = LocalTime.now(ZoneId.of("Europe/Moscow")).truncatedTo(ChronoUnit.SECONDS);
        LocalTime test = LocalTime.parse("08:26:45");
        long duration =  Duration.between(now, test).toMillis();
        String form = DurationFormatUtils.formatDuration(duration, "HH:mm:ss");
        System.out.println(now);
        System.out.println(test);
        System.out.println(duration);
        System.out.println(form);
    }
}
