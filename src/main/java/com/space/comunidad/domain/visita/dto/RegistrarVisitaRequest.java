package com.space.comunidad.domain.visita.dto;

import java.util.List;

import com.space.comunidad.domain.visita.entity.CategoriaVisita;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarVisitaRequest(
    @Size(max = 7) String patente,
    @NotBlank String nombreREsponsable,
    @NotNull @Min(1) Integer cantidadPersonas,
    @NotNull CategoriaVisita categoria,
    @Size(max = 200) String descripcionCategoria,
    @NotEmpty List<Long> unidadesId,
    @Size(max = 500) String notas) {

}
