package com.sigo.asistencia.repository;
import com.sigo.asistencia.entity.Via;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ViaRepository extends JpaRepository<Via,Long> {
 List<Via> findByPlazaIdAndActivaTrueOrderByOrdenAscNumeroAsc(Long plazaId);
}
