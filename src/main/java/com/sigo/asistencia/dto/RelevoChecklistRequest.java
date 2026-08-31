package com.sigo.asistencia.dto;
import com.sigo.asistencia.entity.EstadoOperativo;
import jakarta.validation.constraints.*;
public record RelevoChecklistRequest(@NotNull Long elementoId,@NotNull EstadoOperativo estado,String detalle,@Min(0) Integer cantidad) {}
