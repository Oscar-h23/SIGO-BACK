package com.sigo.asistencia.service;

import com.sigo.asistencia.dto.*;
import com.sigo.asistencia.entity.*;
import com.sigo.asistencia.exception.BusinessException;
import com.sigo.asistencia.exception.ResourceNotFoundException;
import com.sigo.asistencia.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final AsistenciaAusenciaRepository ausenciaRepository;
    private final AsistenciaEvidenciaRepository evidenciaRepository;

    private final PlazaRepository plazaRepository;
    private final TurnoRepository turnoRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final MotivoAusenciaRepository motivoRepository;

    private final CloudinaryService cloudinaryService;

    /*
     * =========================================================
     * REGISTRAR ASISTENCIA
     * =========================================================
     */
    @Transactional
    public AsistenciaResponse registrar(AsistenciaRequest request) {

        Plaza plaza = plazaRepository
                .findById(request.plazaId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Plaza no encontrada")
                );

        Turno turno = turnoRepository
                .findById(request.turnoId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Turno no encontrado")
                );

        Trabajador controlador = trabajadorRepository
                .findById(request.controladorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Controlador no encontrado")
                );

        validarControlador(controlador);

        /*
         * Evitar duplicados por:
         * plaza + turno + fecha
         */
        if (asistenciaRepository.existsByPlazaIdAndTurnoIdAndFecha(
                request.plazaId(),
                request.turnoId(),
                request.fecha()
        )) {
            throw new BusinessException(
                    "Ya existe una asistencia para esa plaza, turno y fecha"
            );
        }

        /*
         * La cantidad programada viene del turno.
         */
        int programados = request.programados();

        int presentes = request.presentes();

        if (presentes < 0 || presentes > programados) {
            throw new BusinessException(
                    "Los presentes deben estar entre 0 y " + programados
            );
        }

        List<AusenciaRequest> ausencias =
                request.ausencias() == null
                        ? List.of()
                        : request.ausencias();

        int cantidadAusentes = programados - presentes;

        if (ausencias.size() != cantidadAusentes) {
            throw new BusinessException(
                    "Debe registrar exactamente "
                            + cantidadAusentes
                            + " ausencia(s)"
            );
        }

        /*
         * Validar que no se repita un trabajador.
         */
        Set<Long> trabajadoresUnicos =
                ausencias.stream()
                        .map(AusenciaRequest::trabajadorId)
                        .collect(Collectors.toSet());

        if (trabajadoresUnicos.size() != ausencias.size()) {
            throw new BusinessException(
                    "No puede registrar al mismo trabajador ausente dos veces"
            );
        }

        /*
         * Guardar asistencia principal.
         */
        AsistenciaRegistro asistencia =
                new AsistenciaRegistro();

        asistencia.setPlaza(plaza);
        asistencia.setTurno(turno);
        asistencia.setControlador(controlador);
        asistencia.setFecha(request.fecha());
        asistencia.setProgramados(programados);
        asistencia.setPresentes(presentes);
        asistencia.setNotas(request.notas());

        asistencia =
                asistenciaRepository.saveAndFlush(asistencia);

        /*
         * Guardar ausencias.
         */
        for (AusenciaRequest ausenciaRequest : ausencias) {

            Trabajador trabajador =
                    trabajadorRepository
                            .findById(ausenciaRequest.trabajadorId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Trabajador no encontrado: "
                                                    + ausenciaRequest.trabajadorId()
                                    )
                            );

            if (!Boolean.TRUE.equals(trabajador.getActivo())) {
                throw new BusinessException(
                        "El trabajador "
                                + trabajador.getCodigo()
                                + " está inactivo"
                );
            }

            MotivoAusencia motivo =
                    motivoRepository
                            .findById(ausenciaRequest.motivoId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Motivo no encontrado: "
                                                    + ausenciaRequest.motivoId()
                                    )
                            );

            AsistenciaAusencia ausencia =
                    new AsistenciaAusencia();

            ausencia.setAsistencia(asistencia);
            ausencia.setTrabajador(trabajador);
            ausencia.setMotivo(motivo);
            ausencia.setObservacion(
                    ausenciaRequest.observacion()
            );

            ausenciaRepository.save(ausencia);
        }

        /*
         * Esto permite mantener compatibilidad con evidencias
         * que ya vengan como URL desde el request.
         */
        if (request.evidencias() != null) {

            for (String url : request.evidencias()) {

                if (url == null || url.isBlank()) {
                    continue;
                }

                AsistenciaEvidencia evidencia =
                        new AsistenciaEvidencia();

                evidencia.setAsistencia(asistencia);
                evidencia.setUrlArchivo(url);
                evidencia.setTipo("foto");

                evidenciaRepository.save(evidencia);
            }
        }

        return obtenerPorId(asistencia.getId());
    }

    /*
     * =========================================================
     * SUBIR FOTO A CLOUDINARY
     * =========================================================
     */
    @Transactional
    public EvidenciaResponse guardarEvidencia(
            Long asistenciaId,
            MultipartFile archivo
    ) throws IOException {

        System.out.println("1. Entrando a guardarEvidencia");

        AsistenciaRegistro asistencia =
                asistenciaRepository.findById(asistenciaId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Asistencia no encontrada"
                                )
                        );

        System.out.println("2. Asistencia encontrada: " + asistencia.getId());

        System.out.println("3. Archivo: " + archivo.getOriginalFilename());
        System.out.println("4. Tipo: " + archivo.getContentType());
        System.out.println("5. Tamaño: " + archivo.getSize());

        Map<String, Object> resultado =
                cloudinaryService.subirImagen(archivo);

        System.out.println("6. Respuesta Cloudinary: " + resultado);

        Object secureUrl =
                resultado.get("secure_url");

        System.out.println("7. URL Cloudinary: " + secureUrl);

        if (secureUrl == null) {
            throw new BusinessException(
                    "Cloudinary no devolvió la URL de la imagen"
            );
        }

        AsistenciaEvidencia evidencia =
                new AsistenciaEvidencia();

        evidencia.setAsistencia(asistencia);
        evidencia.setUrlArchivo(secureUrl.toString());
        evidencia.setTipo("foto");

        System.out.println("8. Guardando evidencia en BD");

        AsistenciaEvidencia guardada =
                evidenciaRepository.save(evidencia);

        System.out.println("9. Evidencia guardada con ID: " + guardada.getId());

        return new EvidenciaResponse(
                guardada.getId(),
                guardada.getUrlArchivo(),
                guardada.getTipo()
        );
    }

    /*
     * =========================================================
     * OBTENER ASISTENCIA POR ID
     * =========================================================
     */
    @Transactional(readOnly = true)
    public AsistenciaResponse obtenerPorId(Long id) {

        AsistenciaRegistro asistencia =
                asistenciaRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Asistencia no encontrada"
                                )
                        );

        List<AusenciaResponse> ausencias =
                ausenciaRepository
                        .findByAsistenciaId(id)
                        .stream()
                        .map(ausencia ->
                                new AusenciaResponse(
                                        ausencia.getId(),
                                        ausencia.getTrabajador().getId(),
                                        ausencia.getTrabajador().getCodigo(),
                                        ausencia.getTrabajador().getNombreCompleto(),
                                        ausencia.getMotivo().getId(),
                                        ausencia.getMotivo().getNombre(),
                                        ausencia.getObservacion()
                                )
                        )
                        .toList();

        List<EvidenciaResponse> evidencias =
                evidenciaRepository
                        .findByAsistenciaId(id)
                        .stream()
                        .map(evidencia ->
                                new EvidenciaResponse(
                                        evidencia.getId(),
                                        evidencia.getUrlArchivo(),
                                        evidencia.getTipo()
                                )
                        )
                        .toList();

        /*
         * Tu AsistenciaResponse también es un record.
         */
        return new AsistenciaResponse(
                asistencia.getId(),

                asistencia.getPlaza().getId(),
                asistencia.getPlaza().getCodigo(),

                asistencia.getTurno().getId(),
                asistencia.getTurno().getCodigo(),

                asistencia.getControlador().getId(),
                asistencia.getControlador().getNombreCompleto(),

                asistencia.getFecha(),

                asistencia.getProgramados(),
                asistencia.getPresentes(),

                asistencia.getProgramados()
                        - asistencia.getPresentes(),

                asistencia.getPorcentaje(),

                asistencia.getNotas(),

                ausencias,
                evidencias
        );
    }

    /*
     * =========================================================
     * LISTAR ASISTENCIAS
     * =========================================================
     */
    @Transactional(readOnly = true)
    public List<AsistenciaResponse> listar(
            LocalDate inicio,
            LocalDate fin
    ) {

        LocalDate fechaInicio =
                inicio != null
                        ? inicio
                        : LocalDate.of(2000, 1, 1);

        LocalDate fechaFin =
                fin != null
                        ? fin
                        : LocalDate.of(2100, 12, 31);

        return asistenciaRepository
                .findByFechaBetweenOrderByFechaDesc(
                        fechaInicio,
                        fechaFin
                )
                .stream()
                .map(asistencia ->
                        obtenerPorId(asistencia.getId())
                )
                .toList();
    }

    /*
     * =========================================================
     * VALIDAR CONTROLADOR
     * =========================================================
     */
    private void validarControlador(
            Trabajador trabajador
    ) {

        if (!Boolean.TRUE.equals(
                trabajador.getActivo()
        )) {
            throw new BusinessException(
                    "El controlador está inactivo"
            );
        }

        String puesto =
                trabajador.getPuesto() == null
                        ? ""
                        : trabajador.getPuesto().getNombre();

        if (!puesto.equalsIgnoreCase("Controlador")
                && !puesto.equalsIgnoreCase("Supervisor")) {

            throw new BusinessException(
                    "El responsable debe ser Controlador o Supervisor"
            );
        }
    }
}