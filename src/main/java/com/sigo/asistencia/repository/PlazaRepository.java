package com.sigo.asistencia.repository;

import com.sigo.asistencia.entity.Plaza;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlazaRepository extends JpaRepository<Plaza, Long> {
    List<Plaza> findByActivoTrueOrderByCodigoAsc();
}