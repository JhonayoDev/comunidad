package com.space.comunidad.domain.visita.dto;

import java.time.LocalDateTime;

import com.space.comunidad.domain.visita.entity.CategoriaVisita;
import com.space.comunidad.domain.visita.entity.VisitaFrecuente;

public record VisitanteFrecuenteResponse(
    String patente,
    String nombreResponsable,
    CategoriaVisita categoria,
    String descripcionCategoria,
    LocalDateTime ultimaVisita) {
  public static VisitanteFrecuenteResponse from(VisitaFrecuente vf) {
    return new VisitanteFrecuenteResponse(
        vf.getPatente(),
        vf.getNombreResponsable(),
        vf.getCategoria(),
        vf.getDescripcionCategoria(),
        vf.getUltimaVisita());
  }
}
