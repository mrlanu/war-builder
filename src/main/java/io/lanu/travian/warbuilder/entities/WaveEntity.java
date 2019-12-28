package io.lanu.travian.warbuilder.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "waves")
public class WaveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String attackId;
    private long timeForAttack;
    @Column(length = 1000)
    private String attackRequest;

    public WaveEntity(String attackId, long timeForAttack, String attackRequest) {
        this.attackId = attackId;
        this.timeForAttack = timeForAttack;
        this.attackRequest = attackRequest;
    }
}
