package io.lanu.travian.warbuilder.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttackRequest {
    private Integer x;
    private Integer y;
    private Integer troops;
}
