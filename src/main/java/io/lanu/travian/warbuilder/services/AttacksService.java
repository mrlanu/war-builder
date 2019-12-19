package io.lanu.travian.warbuilder.services;

import io.lanu.travian.warbuilder.models.AttackRequest;

import java.io.IOException;
import java.util.List;

public interface AttacksService {
    String login();
    void createAttack(List<AttackRequest> attackRequest);
    void addWave(String troops, String x, String y) throws IOException;
}
