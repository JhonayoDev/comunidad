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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/residentes")
@RequiredArgsConstructor
public class ResidenteController {

  private final SectorService sectorService;
  private final UnidadService unidadService;

  @GetMapping("/sectores")
  public ResponseEntity<List<SectorResponse>> listarSectores() {
    return ResponseEntity.ok(sectorService.listarTodos());
  }

  @GetMapping("/sectores/{id}")
  public ResponseEntity<SectorResponse> getSector(@PathVariable Long id) {
    return ResponseEntity.ok(sectorService.buscarPorId(id));
  }

  @PostMapping("/sectores")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SectorResponse> asignarDelegado(@Valid @RequestBody SectorRequest request) {
    return ResponseEntity.ok(sectorService.crear(request));
  }

  @PutMapping("/sectores/{id}/delegado/{usuarioId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SectorResponse> asignarDelegado(@PathVariable Long id, @PathVariable Long usuarioId) {
    return ResponseEntity.ok(sectorService.asignarDelegado(id, usuarioId));
  }

  @PutMapping("/sectores/{id}/delegado/remover")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SectorResponse> removerDelegado(@PathVariable Long id) {
    return ResponseEntity.ok(sectorService.removerDelegado(id));
  }

  @GetMapping("/unidades")
  public ResponseEntity<List<UnidadResponse>> listarUnidades() {
    return ResponseEntity.ok(unidadService.listarTodas());
  }

  @GetMapping("/unidades/{id}")
  public ResponseEntity<UnidadResponse> getUnidad(@PathVariable Long id) {
    return ResponseEntity.ok(unidadService.buscarPorId(id));
  }

  @GetMapping("/sectores/{sectorId}/unidades")
  public ResponseEntity<List<UnidadResponse>> listarPorSectores(@PathVariable Long sectorId) {
    return ResponseEntity.ok(unidadService.listarPorSector(sectorId));
  }

  @PostMapping("/unidades")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UnidadResponse> crearUnidad(@Valid @RequestBody UnidadRequest request) {
    return ResponseEntity.ok(unidadService.crear(request));
  }

  @PutMapping("/unidades/{id}/residente")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UnidadResponse> asignarResidente(@PathVariable Long id,
      @Valid @RequestBody AsignarResidenteRequest request) {
    return ResponseEntity.ok(unidadService.asignarResidente(id, request));
  }

  @PutMapping("/unidades/{id}/propietario/{propietarioId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UnidadResponse> actualizarPropietario(
      @PathVariable Long id,
      @PathVariable Long propietarioId) {
    return ResponseEntity.ok(unidadService.actualizarPropietario(id, propietarioId));
  }

}
