package io.lanu.travian.warbuilder.repositories;

import io.lanu.travian.warbuilder.entities.WaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaveRepository extends JpaRepository<WaveEntity, Long> {
    List<WaveEntity> findAllByAttackId(String attackId);
}
