package com.space.comunidad.domain.residente.dto;

import com.space.comunidad.domain.residente.entity.Unidad;

public record UnidadResponse(
    Long id,
    Integer numero,
    Long sectorId,
    Integer sectorNumero,
    Long propietarioId,
    String propietarioNombre,
    Long residenteActualId,
    String residenteActualNombre) {

  public static UnidadResponse from(Unidad unidad) {
    return new UnidadResponse(
        unidad.getId(),
        unidad.getNumero(),
        unidad.getSector().getId(),
        unidad.getSector().getNumero(),
        unidad.getPropietario().getId(),
        unidad.getPropietario().getNombre(),
        unidad.getResidenteActual() != null ? unidad.getResidenteActual().getId() : null,
        unidad.getResidenteActual() != null ? unidad.getResidenteActual().getNombre() : null);
  }
}
