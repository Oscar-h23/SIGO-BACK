package com.sigo.asistencia.repository;
import com.sigo.asistencia.entity.ElementoRelevo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ElementoRelevoRepository extends JpaRepository<ElementoRelevo,Long> {
 List<ElementoRelevo> findByActivoTrueOrderByCategoriaAscOrdenAscNombreAsc();
}
