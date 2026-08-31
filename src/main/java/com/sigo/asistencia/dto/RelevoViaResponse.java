package com.sigo.asistencia.dto;
import com.sigo.asistencia.entity.EstadoOperativo;
import java.util.List;
public record RelevoViaResponse(
 Long id,Long viaId,Integer numero,String nombre,EstadoOperativo estado,String detalle,
 List<EvidenciaRelevoResponse> evidencias
) {}
