package io.lanu.travian.warbuilder.repositories;

import io.lanu.travian.warbuilder.entities.WaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WaveRepository extends JpaRepository<WaveEntity, Long> {
    List<WaveEntity> findAllByAttackId(String attackId);
    @Transactional
    void deleteAllByAttackId(String attackId);
}
