package io.lanu.travian.warbuilder.services;

import io.lanu.travian.warbuilder.models.AttackRequest;

public interface AttacksService {
    void scheduleAttack(AttackRequest attackRequest);
    void sendSpam(AttackRequest attackRequest);
}
