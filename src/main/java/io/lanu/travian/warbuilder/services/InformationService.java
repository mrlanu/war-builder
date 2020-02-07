package io.lanu.travian.warbuilder.services;

import io.lanu.travian.warbuilder.models.VillageModel;

import java.util.List;

public interface InformationService {
    void changeActiveVillage(String attackingVillageName);
    List<VillageModel> getAllVillages();
    Integer[] getAvailableTroops(String villageName);
}
