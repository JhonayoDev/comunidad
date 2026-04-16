package com.space.comunidad.domain.residente.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.space.comunidad.domain.residente.dto.SectorRequest;
import com.space.comunidad.domain.residente.dto.SectorResponse;
import com.space.comunidad.domain.residente.entity.Sector;
import com.space.comunidad.domain.residente.repository.SectorRepository;
import com.space.comunidad.domain.user.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SectorService {

  private final SectorRepository sectorRepository;
  private final UsuarioRepository usuarioRepository;

  public List<SectorResponse> listarTodos() {
    return sectorRepository.findAll().stream()
        .map(SectorResponse::from)
        .toList();
  }

  public SectorResponse buscarPorId(Long id) {
    return SectorResponse.from(getSector(id));
  }

  @Transactional
  public SectorResponse crear(SectorRequest request) {
    if (sectorRepository.existsByNumero(request.numero())) {
      throw new IllegalArgumentException("Ya existe un sector con el numero: " + request.numero());
    }
    Sector sector = Sector.builder()
        .numero(request.numero())
        .nombre(request.nombre())
        .build();
    return SectorResponse.from(sectorRepository.save(sector));
  }

  @Transactional
  public SectorResponse asignarDelegado(Long sectorId, Long usuarioId) {
    Sector sector = getSector(sectorId);
    sector.setDelegado(usuarioRepository.findById(usuarioId)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + usuarioId)));

    return SectorResponse.from(sectorRepository.save(sector));
  }

  @Transactional
  public SectorResponse removerDelegado(Long sectorId) {
    Sector sector = getSector(sectorId);
    sector.setDelegado(null);
    return SectorResponse.from(sectorRepository.save(sector));
  }

  private Sector getSector(Long id) {
    return sectorRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Sector no encontrado: " + id));
  }

}
