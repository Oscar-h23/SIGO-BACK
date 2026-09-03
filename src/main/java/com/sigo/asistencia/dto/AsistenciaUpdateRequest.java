package com.sigo.asistencia.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record AsistenciaUpdateRequest(

        @NotNull(message = "La plaza es obligatoria")
        Long plazaId,

        @NotNull(message = "El turno es obligatorio")
        Long turnoId,

        @NotNull(message = "El controlador es obligatorio")
        Long controladorId,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotNull(message = "La cantidad de programados es obligatoria")
        @Min(
                value = 0,
                message = "La cantidad de programados no puede ser negativa"
        )
        Integer programados,

        @NotNull(message = "La cantidad de presentes es obligatoria")
        @Min(
                value = 0,
                message = "La cantidad de presentes no puede ser negativa"
        )
        Integer presentes,

        String notas,

        @Valid
        List<AusenciaRequest> ausencias

) {
}