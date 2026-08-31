package com.sigo.asistencia.controller;
import com.sigo.asistencia.dto.ViaResponse;
import com.sigo.asistencia.service.ViaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/vias") @RequiredArgsConstructor
public class ViaController {
 private final ViaService viaService;
 @GetMapping public List<ViaResponse> listarPorPlaza(@RequestParam Long plazaId){ return viaService.listarPorPlaza(plazaId); }
}
