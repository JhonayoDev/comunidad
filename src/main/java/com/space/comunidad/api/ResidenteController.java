package com.space.comunidad.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.space.comunidad.domain.residente.dto.AsignarResidenteRequest;
import com.space.comunidad.domain.residente.dto.SectorRequest;
import com.space.comunidad.domain.residente.dto.SectorResponse;
import com.space.comunidad.domain.residente.dto.UnidadRequest;
import com.space.comunidad.domain.residente.dto.UnidadResponse;
import com.space.comunidad.domain.residente.service.SectorService;
import com.space.comunidad.domain.residente.service.UnidadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/residentes")
@RequiredArgsConstructor
public class ResidenteController {

  private final SectorService sectorService;
  private final UnidadService unidadService;

  // ─── Sectores ─────────────────────────────────────────────────────────────

  @GetMapping("/sectores")
  @Tag(name = "Sectores", description = "Gestión de sectores del condominio")
  @Operation(summary = "Listar sectores", description = "Retorna todos los sectores del condominio.")
  @ApiResponse(responseCode = "200", description = "Lista de sectores")
  @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
  public ResponseEntity<List<SectorResponse>> listarSectores() {
    return ResponseEntity.ok(sectorService.listarTodos());
  }

  @GetMapping("/sectores/{id}")
  @Tag(name = "Sectores")
  @Operation(summary = "Obtener sector", description = "Retorna el detalle de un sector por su ID.")
  @ApiResponse(responseCode = "200", description = "Sector encontrado", content = @Content(schema = @Schema(implementation = SectorResponse.class)))
  @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
  @ApiResponse(responseCode = "500", description = "Sector no encontrado")
  public ResponseEntity<SectorResponse> getSector(
      @Parameter(description = "ID del sector") @PathVariable Long id) {
    return ResponseEntity.ok(sectorService.buscarPorId(id));
  }

  @PostMapping("/sectores")
  @PreAuthorize("hasRole('ADMIN')")
  @Tag(name = "Sectores")
  @Operation(summary = "Crear sector", description = "Crea un nuevo sector. Requiere rol **ADMIN**.")
  @ApiResponse(responseCode = "200", description = "Sector creado", content = @Content(schema = @Schema(implementation = SectorResponse.class)))
  @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
  @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
  @ApiResponse(responseCode = "500", description = "Número de sector duplicado")
  public ResponseEntity<SectorResponse> crearSector(@Valid @RequestBody SectorRequest request) {
    return ResponseEntity.ok(sectorService.crear(request));
  }

  @PutMapping("/sectores/{id}/delegado/{usuarioId}")
  @PreAuthorize("hasRole('ADMIN')")
  @Tag(name = "Sectores")
  @Operation(summary = "Asignar delegado", description = "Asigna un usuario como delegado del sector. Requiere rol **ADMIN**.")
  @ApiResponse(responseCode = "200", description = "Delegado asignado", content = @Content(schema = @Schema(implementation = SectorResponse.class)))
  @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
  @ApiResponse(responseCode = "500", description = "Sector o usuario no encontrado")
  public ResponseEntity<SectorResponse> asignarDelegado(
      @Parameter(description = "ID del sector") @PathVariable Long id,
      @Parameter(description = "ID del usuario a asignar como delegado") @PathVariable Long usuarioId) {
    return ResponseEntity.ok(sectorService.asignarDelegado(id, usuarioId));
  }

  @PutMapping("/sectores/{id}/delegado/remover")
  @PreAuthorize("hasRole('ADMIN')")
  @Tag(name = "Sectores")
  @Operation(summary = "Remover delegado", description = "Elimina el delegado actual del sector. Requiere rol **ADMIN**.")
  @ApiResponse(responseCode = "200", description = "Delegado removido")
  @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
  @ApiResponse(responseCode = "500", description = "Sector no encontrado")
  public ResponseEntity<SectorResponse> removerDelegado(
      @Parameter(description = "ID del sector") @PathVariable Long id) {
    return ResponseEntity.ok(sectorService.removerDelegado(id));
  }

  // ─── Unidades ─────────────────────────────────────────────────────────────

  @GetMapping("/unidades")
  @Tag(name = "Unidades", description = "Gestión de unidades (casas) del condominio")
  @Operation(summary = "Listar unidades", description = "Retorna todas las unidades del condominio.")
  @ApiResponse(responseCode = "200", description = "Lista de unidades")
  @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
  public ResponseEntity<List<UnidadResponse>> listarUnidades() {
    return ResponseEntity.ok(unidadService.listarTodas());
  }

  @GetMapping("/unidades/{id}")
  @Tag(name = "Unidades")
  @Operation(summary = "Obtener unidad", description = "Retorna el detalle de una unidad por su ID.")
  @ApiResponse(responseCode = "200", description = "Unidad encontrada", content = @Content(schema = @Schema(implementation = UnidadResponse.class)))
  @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
  @ApiResponse(responseCode = "500", description = "Unidad no encontrada")
  public ResponseEntity<UnidadResponse> getUnidad(
      @Parameter(description = "ID de la unidad") @PathVariable Long id) {
    return ResponseEntity.ok(unidadService.buscarPorId(id));
  }

  @GetMapping("/sectores/{sectorId}/unidades")
  @Tag(name = "Unidades")
  @Operation(summary = "Listar unidades por sector", description = "Retorna todas las unidades pertenecientes a un sector específico.")
  @ApiResponse(responseCode = "200", description = "Lista de unidades del sector")
  @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
  public ResponseEntity<List<UnidadResponse>> listarPorSectores(
      @Parameter(description = "ID del sector") @PathVariable Long sectorId) {
    return ResponseEntity.ok(unidadService.listarPorSector(sectorId));
  }

  @PostMapping("/unidades")
  @PreAuthorize("hasRole('ADMIN')")
  @Tag(name = "Unidades")
  @Operation(summary = "Crear unidad", description = "Crea una nueva unidad en el condominio. Requiere rol **ADMIN**.")
  @ApiResponse(responseCode = "200", description = "Unidad creada", content = @Content(schema = @Schema(implementation = UnidadResponse.class)))
  @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
  @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
  @ApiResponse(responseCode = "500", description = "Número duplicado, sector o propietario no encontrado")
  public ResponseEntity<UnidadResponse> crearUnidad(@Valid @RequestBody UnidadRequest request) {
    return ResponseEntity.ok(unidadService.crear(request));
  }

  @PutMapping("/unidades/{id}/residente")
  @PreAuthorize("hasRole('ADMIN')")
  @Tag(name = "Unidades")
  @Operation(summary = "Asignar residente", description = """
      Asigna o reemplaza el residente actual de una unidad. Requiere rol **ADMIN**.

      Si la unidad ya tenía un residente, su registro en el historial se cierra \
      automáticamente (`fechaFin = fechaInicio - 1 día`).
      """)
  @ApiResponse(responseCode = "200", description = "Residente asignado", content = @Content(schema = @Schema(implementation = UnidadResponse.class)))
  @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
  @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
  @ApiResponse(responseCode = "500", description = "Unidad o usuario no encontrado")
  public ResponseEntity<UnidadResponse> asignarResidente(
      @Parameter(description = "ID de la unidad") @PathVariable Long id,
      @Valid @RequestBody AsignarResidenteRequest request) {
    return ResponseEntity.ok(unidadService.asignarResidente(id, request));
  }

  @PutMapping("/unidades/{id}/propietario/{propietarioId}")
  @PreAuthorize("hasRole('ADMIN')")
  @Tag(name = "Unidades")
  @Operation(summary = "Actualizar propietario", description = "Cambia el propietario de una unidad. Requiere rol **ADMIN**.")
  @ApiResponse(responseCode = "200", description = "Propietario actualizado", content = @Content(schema = @Schema(implementation = UnidadResponse.class)))
  @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
  @ApiResponse(responseCode = "500", description = "Unidad o usuario no encontrado")
  public ResponseEntity<UnidadResponse> actualizarPropietario(
      @Parameter(description = "ID de la unidad") @PathVariable Long id,
      @Parameter(description = "ID del nuevo propietario") @PathVariable Long propietarioId) {
    return ResponseEntity.ok(unidadService.actualizarPropietario(id, propietarioId));
  }

}
