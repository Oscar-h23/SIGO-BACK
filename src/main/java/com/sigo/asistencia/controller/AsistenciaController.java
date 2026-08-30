package com.sigo.asistencia.controller;

import com.sigo.asistencia.dto.AsistenciaRequest;
import com.sigo.asistencia.dto.AsistenciaResponse;
import com.sigo.asistencia.dto.EvidenciaResponse;
import com.sigo.asistencia.service.AsistenciaService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    /*
     * Registrar asistencia
     */
    @PostMapping
    public ResponseEntity<AsistenciaResponse> registrar(
            @Valid @RequestBody AsistenciaRequest request
    ) {

        return ResponseEntity.ok(
                asistenciaService.registrar(request)
        );
    }

    /*
     * Listar asistencias
     *
     * Ejemplo:
     * GET /api/asistencias
     *
     * GET /api/asistencias?inicio=2026-08-01&fin=2026-08-31
     */
    @GetMapping
    public ResponseEntity<List<AsistenciaResponse>> listar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fin
    ) {

        return ResponseEntity.ok(
                asistenciaService.listar(inicio, fin)
        );
    }

    /*
     * Obtener por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaResponse> obtenerPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                asistenciaService.obtenerPorId(id)
        );
    }

    /*
     * Subir foto a Cloudinary
     *
     * POST /api/asistencias/1/evidencias
     *
     * Body -> form-data
     * file -> imagen
     */
    @PostMapping(
            value = "/{id}/evidencias",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<EvidenciaResponse> subirEvidencia(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        return ResponseEntity.ok(
                asistenciaService.guardarEvidencia(id, file)
        );
    }
}