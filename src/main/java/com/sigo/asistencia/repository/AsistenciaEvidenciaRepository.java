package com.sigo.asistencia.repository;

import com.sigo.asistencia.entity.AsistenciaEvidencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsistenciaEvidenciaRepository
        extends JpaRepository<AsistenciaEvidencia, Long> {

    /*
     * Obtener todas las evidencias
     * de una asistencia.
     */
    List<AsistenciaEvidencia> findByAsistenciaId(
            Long asistenciaId
    );

    /*
     * Obtener una evidencia verificando
     * que realmente pertenece a la asistencia.
     *
     * Se utilizará para eliminar fotografías
     * durante la edición.
     */
    Optional<AsistenciaEvidencia>
    findByIdAndAsistenciaId(
            Long evidenciaId,
            Long asistenciaId
    );
}