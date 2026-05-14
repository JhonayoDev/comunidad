package com.space.comunidad.domain.residente.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.space.comunidad.domain.residente.dto.AsignarResidenteRequest;
import com.space.comunidad.domain.residente.dto.UnidadRequest;
import com.space.comunidad.domain.residente.dto.UnidadResponse;
import com.space.comunidad.domain.residente.entity.HistorialResidente;
import com.space.comunidad.domain.residente.entity.Unidad;
import com.space.comunidad.domain.residente.repository.HistorialResidenteRepository;
import com.space.comunidad.domain.residente.repository.SectorRepository;
import com.space.comunidad.domain.residente.repository.UnidadRepository;
import com.space.comunidad.domain.user.entity.Usuario;
import com.space.comunidad.domain.user.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnidadService {

  private final UnidadRepository unidadRepository;
  private final SectorRepository sectorRepository;
  private final UsuarioRepository usuarioRepository;
  private final HistorialResidenteRepository historialRepository;

  public List<UnidadResponse> listarTodas() {
    return unidadRepository.findAll().stream()
        .map(UnidadResponse::from)
        .toList();
  }

  public UnidadResponse buscarPorId(Long id) {
    return UnidadResponse.from(getUnidad(id));
  }

  public List<UnidadResponse> listarPorSector(Long sectorId) {
    return unidadRepository.findBySectorId(sectorId).stream()
        .map(UnidadResponse::from)
        .toList();
  }

  @Transactional
  public UnidadResponse crear(UnidadRequest request) {
    if (unidadRepository.existsByNumero(request.numero())) {
      throw new IllegalArgumentException("Ya existe una unidad con en numero " + request.numero());
    }

    Unidad unidad = Unidad.builder()
        .numero(request.numero())
        .sector(sectorRepository.findById(request.sectorId())
            .orElseThrow(() -> new IllegalArgumentException("Sector no encontrado: " + request.sectorId())))
        .propietario(getUsuario(request.propietarioId()))
        .build();

    return UnidadResponse.from(unidadRepository.save(unidad));

  }

  @Transactional
  public UnidadResponse asignarResidente(Long unidadId, AsignarResidenteRequest request) {
    Unidad unidad = getUnidad(unidadId);
    Usuario nuevoResidente = getUsuario(request.usuarioId());

    historialRepository.findByUnidadIdAndFechaFinIsNull(unidadId)
        .ifPresent(historial -> {
          historial.setFechaFin(request.fechaInicio().minusDays(1));
          historialRepository.save(historial);
        });

    historialRepository.save(HistorialResidente.builder()
        .unidad(unidad)
        .usuario(nuevoResidente)
        .fechaInicio(request.fechaInicio())
        .build());

    unidad.setResidenteActual(nuevoResidente);
    return UnidadResponse.from(unidadRepository.save(unidad));
  }

  @Transactional
  public UnidadResponse actualizarPropietario(Long unidadId, Long propietarioId) {
    Unidad unidad = getUnidad(unidadId);
    unidad.setPropietario(getUsuario(propietarioId));
    return UnidadResponse.from(unidadRepository.save(unidad));
  }

  private Unidad getUnidad(Long id) {
    return unidadRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada: " + id));
  }

  private Usuario getUsuario(Long id) {
    return usuarioRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
  }

}
