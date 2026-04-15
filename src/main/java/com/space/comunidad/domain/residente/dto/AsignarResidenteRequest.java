package com.space.comunidad.domain.residente.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record AsignarResidenteRequest(
    @NotNull Long usuarioId,
    @NotNull LocalDate fechaInicio) {

}
