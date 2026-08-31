package com.sigo.asistencia.repository;
import com.sigo.asistencia.entity.RelevoChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RelevoChecklistRepository extends JpaRepository<RelevoChecklist,Long> {
 List<RelevoChecklist> findByRelevoIdOrderByElementoCategoriaAscElementoOrdenAsc(Long relevoId);
}
