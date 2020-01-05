package io.lanu.travian.warbuilder.models;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AttackRequest implements Serializable {
    private String attackId;
    private Integer x;
    private Integer y;
    private Integer kindAttack;
    private Integer[] troops;
    private Integer firstTarget;
    private Integer secondTarget;
    private Date time; // format HH:MM:SS
}
