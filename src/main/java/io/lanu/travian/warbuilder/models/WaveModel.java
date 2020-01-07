package io.lanu.travian.warbuilder.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WaveModel {
    private Integer[] troops;
    private Integer firstTarget;
    private Integer secondTarget;
}
