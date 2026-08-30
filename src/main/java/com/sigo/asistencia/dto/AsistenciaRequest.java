package com.sigo.asistencia.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record AsistenciaRequest(

        @NotNull
        Long plazaId,

        @NotNull
        Long turnoId,

        @NotNull
        Long controladorId,

        @NotNull
        LocalDate fecha,

        @NotNull
        @Min(1)
        Integer programados,

        @NotNull
        @Min(0)
        Integer presentes,

        String notas,

        @Valid
        List<AusenciaRequest> ausencias,

        List<@NotBlank String> evidencias

) {}