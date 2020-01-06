package io.lanu.travian.warbuilder.services;

import io.lanu.travian.warbuilder.models.VillageModel;

import java.util.List;

public interface InformationService {
    List<VillageModel> getAllVillages();
    Integer[] getAvailableTroops(String villageName);
}
