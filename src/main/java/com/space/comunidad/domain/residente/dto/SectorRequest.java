package com.space.comunidad.domain.residente.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SectorRequest(
    @NotNull @Positive Integer numero,
    String nombre) {
}
