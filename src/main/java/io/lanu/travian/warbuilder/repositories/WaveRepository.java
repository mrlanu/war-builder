package io.lanu.travian.warbuilder.repositories;

import io.lanu.travian.warbuilder.entities.WaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaveRepository extends JpaRepository<WaveEntity, Long> {
}
