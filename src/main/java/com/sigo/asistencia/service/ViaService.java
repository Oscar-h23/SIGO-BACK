package com.sigo.asistencia.service;
import com.sigo.asistencia.dto.ViaResponse;
import com.sigo.asistencia.exception.ResourceNotFoundException;
import com.sigo.asistencia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class ViaService {
 private final ViaRepository viaRepository;
 private final PlazaRepository plazaRepository;
 public List<ViaResponse> listarPorPlaza(Long plazaId){
  if(!plazaRepository.existsById(plazaId)) throw new ResourceNotFoundException("Plaza no encontrada");
  return viaRepository.findByPlazaIdAndActivaTrueOrderByOrdenAscNumeroAsc(plazaId).stream()
   .map(v->new ViaResponse(v.getId(),v.getPlaza().getId(),v.getNumero(),v.getNombre(),v.getActiva(),v.getOrden())).toList();
 }
}
