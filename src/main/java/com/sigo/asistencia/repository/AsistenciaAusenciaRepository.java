package com.sigo.asistencia.repository;

import com.sigo.asistencia.entity.AsistenciaAusencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsistenciaAusenciaRepository
        extends JpaRepository<AsistenciaAusencia, Long> {

 /*
  * Obtener ausencias pertenecientes
  * a una asistencia.
  */
 List<AsistenciaAusencia> findByAsistenciaId(
         Long asistenciaId
 );

 /*
  * Eliminar todas las ausencias de
  * una asistencia.
  *
  * Se utilizará cuando se edite
  * el registro de asistencia.
  */
 void deleteByAsistenciaId(
         Long asistenciaId
 );

 /*
  * Contar las ausencias agrupadas por motivo.
  *
  * Si mes es null:
  * devuelve todo el año.
  *
  * Si mes tiene valor:
  * filtra por año y mes.
  */
 @Query(
         value = """
                    SELECT
                        ma.nombre,
                        COUNT(aa.id)
                    FROM asistencia_ausencia aa
                    INNER JOIN asistencia_registro ar
                        ON ar.id = aa.asistencia_id
                    INNER JOIN motivos_ausencia ma
                        ON ma.id = aa.motivo_id
                    WHERE EXTRACT(YEAR FROM ar.fecha) = :anio
                      AND (
                            :mes IS NULL
                            OR EXTRACT(MONTH FROM ar.fecha) = :mes
                          )
                    GROUP BY ma.id, ma.nombre
                    ORDER BY COUNT(aa.id) DESC
                    """,
         nativeQuery = true
 )
 List<Object[]> contarPorMotivo(
         @Param("anio") int anio,
         @Param("mes") Integer mes
 );
}