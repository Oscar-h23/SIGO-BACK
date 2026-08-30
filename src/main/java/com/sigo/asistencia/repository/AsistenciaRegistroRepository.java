package com.sigo.asistencia.repository;

import com.sigo.asistencia.entity.AsistenciaRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AsistenciaRegistroRepository
        extends JpaRepository<AsistenciaRegistro, Long> {

    boolean existsByPlazaIdAndTurnoIdAndFecha(
            Long plazaId,
            Long turnoId,
            LocalDate fecha
    );
}