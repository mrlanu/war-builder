package io.lanu.travian.warbuilder.services;

import io.lanu.travian.warbuilder.models.AttackRequest;

import java.util.List;

public interface AttacksService {
    List<AttacksServiceImpl.Coordinates> getVillagesForSpam();
    void scheduleAttack(AttackRequest attackRequest);
}
