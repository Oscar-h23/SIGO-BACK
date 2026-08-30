package com.sigo.asistencia.repository;

import com.sigo.asistencia.entity.AsistenciaEvidencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsistenciaEvidenciaRepository
        extends JpaRepository<AsistenciaEvidencia, Long> {

    List<AsistenciaEvidencia> findByAsistenciaId(Long asistenciaId);
}