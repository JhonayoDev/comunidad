package com.space.comunidad.domain.residente.dto;

import com.space.comunidad.domain.residente.entity.Sector;

public record SectorResponse(
    Long id,
    Integer numero,
    String nombre,
    Long delegadoId,
    String delegadoNombre) {

  public static SectorResponse from(Sector sector) {
    return new SectorResponse(
        sector.getId(),
        sector.getNumero(),
        sector.getNombre(),
        sector.getDelegado() != null ? sector.getDelegado().getId() : null,
        sector.getDelegado() != null ? sector.getDelegado().getNombre() : null);
  }
}
