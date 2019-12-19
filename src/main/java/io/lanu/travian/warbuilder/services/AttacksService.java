package io.lanu.travian.warbuilder.services;

import io.lanu.travian.warbuilder.models.AttackRequest;

import java.util.List;

public interface AttacksService {
    String login();
    void createAttack(List<AttackRequest> attackRequest);
}
