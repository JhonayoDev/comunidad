package com.space.comunidad.domain.residente.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UnidadRequest(
    @NotNull @Positive Integer numero,
    @NotNull Long sectorId,
    @NotNull Long propietarioId) {

}
