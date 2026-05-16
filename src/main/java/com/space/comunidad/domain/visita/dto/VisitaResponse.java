package com.space.comunidad.domain.visita.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.space.comunidad.domain.visita.entity.CategoriaVisita;
import com.space.comunidad.domain.visita.entity.Visita;

public record VisitaResponse(
    Long id,
    String patente,
    String nombreResponsable,
    Integer cantidadPersonas,
    CategoriaVisita categoria,
    String descripcionCategoria,
    LocalDateTime horaIngreso,
    LocalDateTime horaSalida,
    String notas,
    Long registradoPorId,
    String registradoPorNombre,
    List<UnidadDestinoResponse> unidades) {
  public record UnidadDestinoResponse(
      Long unidadId,
      Integer unidadNumero) {
  }

  public static VisitaResponse from(Visita visita) {
    return new VisitaResponse(
        visita.getId(),
        visita.getPatente(),
        visita.getNombreResponsable(),
        visita.getCantidadPersonas(),
        visita.getCategoriaVisita(),
        visita.getDescripcionCategoria(),
        visita.getHoraIngreso(),
        visita.getHoraSalida(),
        visita.getNotas(),
        visita.getRegistradoPor().getId(),
        visita.getRegistradoPor().getNombre(),
        visita.getUnidades().stream()
            .map(vu -> new UnidadDestinoResponse(vu.getUnidad().getId(),
                vu.getUnidad().getNumero()))
            .toList());

  }
}
