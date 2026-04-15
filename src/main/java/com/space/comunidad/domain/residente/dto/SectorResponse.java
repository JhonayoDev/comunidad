package com.space.comunidad.domain.residente.dto;

public record SectorResponse(
    Long id,
    Integer numero,
    String nombre,
    Long delegadoId,
    String delegadoNombre) {

}
