package com.space.comunidad.domain.visita.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.space.comunidad.domain.residente.entity.Unidad;
import com.space.comunidad.domain.residente.repository.UnidadRepository;
import com.space.comunidad.domain.user.entity.Usuario;
import com.space.comunidad.domain.user.repository.UsuarioRepository;
import com.space.comunidad.domain.visita.dto.RegistrarVisitaRequest;
import com.space.comunidad.domain.visita.dto.VisitaResponse;
import com.space.comunidad.domain.visita.dto.VisitanteFrecuenteResponse;
import com.space.comunidad.domain.visita.entity.CategoriaVisita;
import com.space.comunidad.domain.visita.entity.Visita;
import com.space.comunidad.domain.visita.entity.VisitaFrecuente;
import com.space.comunidad.domain.visita.entity.VisitaUnidad;
import com.space.comunidad.domain.visita.repository.VisitaFrecuenteRepository;
import com.space.comunidad.domain.visita.repository.VisitaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitaService {

  private final VisitaRepository visitaRepository;
  private final VisitaFrecuenteRepository frecuenteRepository;
  private final UnidadRepository unidadRepository;
  private final UsuarioRepository usuarioRepository;

  @Transactional
  public VisitaResponse registrarIngreso(RegistrarVisitaRequest request, Long guardiaId) {

    validarCategoria(request);

    Usuario guardia = getUsuario(guardiaId);
    List<Unidad> unidades = resolverUnidades(request.unidadesId());

    Visita visita = Visita.builder()
        .patente(normalizarPatente(request.patente()))
        .nombreResponsable(request.nombreResponsable())
        .cantidadPersonas(request.cantidadPersonas())
        .categoria(request.categoria())
        .descripcionCategoria(request.descripcionCategoria())
        .horaIngreso(LocalDateTime.now())
        .registradoPor(guardia)
        .build();

    unidades.forEach(unidad -> visita.getUnidades().add(
        VisitaUnidad.builder()
            .visita(visita)
            .unidad(unidad)
            .build()));

    Visita guardada = visitaRepository.save(visita);

    if (guardada.getPatente() != null) {
      actualizarVisitanteFrecuente(guardada);
    }
    return VisitaResponse.from(guardada);
  }

  @Transactional
  public VisitaResponse registrarSalida(Long visitaId) {
    Visita visita = getVisita(visitaId);

    if (visita.getHoraSalida() != null) {
      throw new IllegalArgumentException(
          "La visita ya tiene hora de salida registrada");
    }
    visita.setHoraSalida(LocalDateTime.now());
    return VisitaResponse.from(visitaRepository.save(visita));
  }

  public List<VisitaResponse> listarTodas() {
    return visitaRepository.findAll().stream()
        .map(VisitaResponse::from)
        .toList();
  }

  public VisitaResponse buscarPorId(Long id) {
    return VisitaResponse.from(getVisita(id));
  }

  public List<VisitaResponse> buscarPorPatente(String patente) {
    return visitaRepository.findByNombreResponsableContainingIgnoreCase(patente).stream()
        .map(VisitaResponse::from)
        .toList();
  }

  public List<VisitaResponse> buscarPorNombre(String nombre) {
    return visitaRepository.findByNombreResponsableContainingIgnoreCase(nombre).stream()
        .map(VisitaResponse::from)
        .toList();
  }

  public List<VisitaResponse> listarActivas() {
    return visitaRepository.findByHoraSalidaIsNull().stream()
        .map(VisitaResponse::from)
        .toList();
  }

  public List<VisitaResponse> listarPorCategoria(CategoriaVisita categoria) {
    return visitaRepository.findByCategoria(categoria).stream()
        .map(VisitaResponse::from)
        .toList();
  }

  public List<VisitaResponse> listarPorUnidad(Long unidadId) {
    return visitaRepository.findByUnidadId(unidadId).stream()
        .map(VisitaResponse::from)
        .toList();
  }

  public List<VisitanteFrecuenteResponse> sugerenciasPorPatente(String patente) {
    return frecuenteRepository.findByPatenteStartingWithIgnoreCase(patente).stream()
        .map(VisitanteFrecuenteResponse::from)
        .toList();
  }

  // Helpers

  private void validarCategoria(RegistrarVisitaRequest request) {
    if (request.categoria() == CategoriaVisita.OTRO
        && (request.descripcionCategoria() == null || request.descripcionCategoria().isBlank())) {
      throw new IllegalArgumentException(
          "Descripcion de categoria es obligatoria cuando la categoria es OTRO");
    }
  }

  private String normalizarPatente(String patente) {
    if (patente == null || patente.isBlank())
      return null;

    return patente.trim().toUpperCase();
  }

  private List<Unidad> resolverUnidades(List<Long> ids) {
    List<Unidad> unidades = unidadRepository.findAllById(ids);

    if (unidades.size() != ids.size()) {
      throw new IllegalArgumentException(
          " Una o mas unidades no fueron encontradas");
    }

    return unidades;
  }

  private void actualizarVisitanteFrecuente(Visita visita) {
    VisitaFrecuente frecuente = frecuenteRepository
        .findById(visita.getPatente())
        .orElse(VisitaFrecuente.builder()
            .patente(visita.getPatente())
            .build());

    frecuente.setNombreResponsable(visita.getNombreResponsable());
    frecuente.setCategoria(visita.getCategoria());
    frecuente.setDescripcionCategoria(visita.getDescripcionCategoria());
    frecuente.setUltimaVisita(visita.getHoraIngreso());

    frecuenteRepository.save(frecuente);
  }

  private Visita getVisita(Long id) {
    return visitaRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Visita no encontrada: " + id));
  }

  private Usuario getUsuario(Long id) {
    return usuarioRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(
            "Usuario no encontrado con id:  " + id));
  }

}
