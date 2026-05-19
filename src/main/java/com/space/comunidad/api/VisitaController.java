package com.space.comunidad.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.space.comunidad.domain.user.entity.Usuario;
import com.space.comunidad.domain.visita.dto.RegistrarVisitaRequest;
import com.space.comunidad.domain.visita.dto.VisitaResponse;
import com.space.comunidad.domain.visita.dto.VisitanteFrecuenteResponse;
import com.space.comunidad.domain.visita.entity.CategoriaVisita;
import com.space.comunidad.domain.visita.service.VisitaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/visitas")
@RequiredArgsConstructor
@Tag(name = "Visitas", description = "Registro y consulta de visitas al condominio")
public class VisitaController {

  private final VisitaService visitaService;

  @GetMapping
  @PreAuthorize("hasAnyRole('GUARDIA', 'ADMIN')")
  @Operation(summary = "Listar visitas", description = """
      Lista visitas con filtros opcionales.
      Si no se indica ningún filtro retorna todas las visitas.
      Los filtros `patente` y `nombre` son parciales e insensibles a mayúsculas.
      """)
  public ResponseEntity<List<VisitaResponse>> listar(
      @Parameter(description = "Filtrar por patente (parcial)") @RequestParam(required = false) String patente,
      @Parameter(description = "Filtrar por nombre responsable (parcial)") @RequestParam(required = false) String nombre,
      @Parameter(description = "Filtrar por categoría") @RequestParam(required = false) CategoriaVisita categoria,
      @Parameter(description = "Solo visitas activas (sin hora de salida)") @RequestParam(required = false) Boolean activa,
      @Parameter(description = "Filtrar por unidad destino") @RequestParam(required = false) Long unidadId) {

    if (patente != null)
      ResponseEntity.ok(visitaService.buscarPorPatente(patente));
    if (nombre != null)
      ResponseEntity.ok(visitaService.buscarPorNombre(nombre));
    if (categoria != null)
      ResponseEntity.ok(visitaService.listarPorCategoria(categoria));
    if (Boolean.TRUE.equals(activa))
      ResponseEntity.ok(visitaService.listarActivas());
    if (unidadId != null)
      ResponseEntity.ok(visitaService.listarPorUnidad(unidadId));

    return ResponseEntity.ok(visitaService.listarTodas());
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('GUARDIA', 'ADMIN')")
  @Operation(summary = "Registro de ingreso", description = "Registra el ingreso de una visita. El guardia autenticado queda como responsable del registro.")
  public ResponseEntity<VisitaResponse> registrarIngreso(
      @Valid @RequestBody RegistrarVisitaRequest request,
      @AuthenticationPrincipal Usuario guardia) {
    return ResponseEntity.ok(visitaService.registrarIngreso(request, guardia.getId()));
  }

  @PutMapping("/{id}/salida")
  @PreAuthorize("hasAnyRole('GUARDIA', 'ADMIN')")
  @Operation(summary = "Obtener visita", description = "Retorna el detalle de una visita por su ID.")
  public ResponseEntity<VisitaResponse> registrarSalida(
      @Parameter(description = "ID de la visita") @PathVariable Long id) {
    return ResponseEntity.ok(visitaService.registrarSalida(id));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('GUARDIA', 'ADMIN')")
  @Operation(summary = "Obtener Visita", description = " Retorna el detalle de una visita por su Id")
  public ResponseEntity<VisitaResponse> getVisita(
      @Parameter(description = "Id de la visita") @PathVariable Long id) {
    return ResponseEntity.ok(visitaService.buscarPorId(id));
  }

  @GetMapping("/frecuente")
  @PreAuthorize("hasAnyRole('GUARDIA', 'ADMIN')")
  @Operation(summary = "Sugerencias por patente", description = "Retorna visitante frecuentes cua patente comienza con el texto ingresado. Usado para autocompletado en el formulario de ingreso")
  public ResponseEntity<List<VisitanteFrecuenteResponse>> sugerencias(
      @Parameter(description = "Inicio de la patente", required = true) @RequestParam String patente) {
    return ResponseEntity.ok(visitaService.sugerenciasPorPatente(patente));
  }
}
