package io.lanu.travian.warbuilder.services;

import java.util.Map;

public interface InformationService {
    Map<String, String> getAllVillages();
    Integer[] getAvailableTroops();
}
