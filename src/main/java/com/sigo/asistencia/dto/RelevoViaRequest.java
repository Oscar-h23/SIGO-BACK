package com.sigo.asistencia.dto;
import com.sigo.asistencia.entity.EstadoOperativo;
import jakarta.validation.constraints.NotNull;
public record RelevoViaRequest(@NotNull Long viaId,@NotNull EstadoOperativo estado,String detalle) {}
