package io.lanu.travian.warbuilder.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "waves")
public class WaveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String waveId;
}
