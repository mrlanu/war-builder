package io.lanu.travian.warbuilder.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AboutVillageModel {
    private String name;
    private Integer[] availableTroops;
    private List<VillageModel> allVillageList;
}
