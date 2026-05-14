package com.space.comunidad.domain.residente;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.space.comunidad.TestcontainersConfiguration;
import com.space.comunidad.domain.residente.dto.AsignarResidenteRequest;
import com.space.comunidad.domain.residente.dto.SectorRequest;
import com.space.comunidad.domain.residente.dto.SectorResponse;
import com.space.comunidad.domain.residente.dto.UnidadRequest;
import com.space.comunidad.domain.residente.dto.UnidadResponse;
import com.space.comunidad.domain.residente.repository.HistorialResidenteRepository;
import com.space.comunidad.domain.residente.service.SectorService;
import com.space.comunidad.domain.residente.service.UnidadService;
import com.space.comunidad.domain.user.entity.Role;
import com.space.comunidad.domain.user.entity.Usuario;
import com.space.comunidad.domain.user.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class UnidadServiceTest {

  @Autowired
  UnidadService unidadService;

  @Autowired
  SectorService sectorService;

  @Autowired
  UsuarioRepository usuarioRepository;

  @Autowired
  HistorialResidenteRepository historialRepository;

  private Long sectorId;
  private Long propietarioId;

  @BeforeEach
  void setup() {
    SectorResponse sector = sectorService.crear(new SectorRequest(10, "Sector Test"));
    sectorId = sector.id();

    Usuario propietario = usuarioRepository.save(Usuario.builder()
        .nombre("Carlos Propietario")
        .email("carlos@email.com")
        .password("pass")
        .role(Role.RESIDENTE)
        .build());
    propietarioId = propietario.getId();
  }

  @Test
  void crearUnidad_exitoso() {
    UnidadResponse response = unidadService.crear(new UnidadRequest(101, sectorId, propietarioId));

    assertThat(response.id()).isNotNull();
    assertThat(response.numero()).isEqualTo(101);
    assertThat(response.sectorId()).isEqualTo(sectorId);
    assertThat(response.propietarioId()).isEqualTo(propietarioId);
    assertThat(response.residenteActualId()).isNull();
  }

  @Test
  void crearUnidad_numeroDuplicado_lanzaExcepcion() {
    unidadService.crear(new UnidadRequest(102, sectorId, propietarioId));

    assertThatThrownBy(() -> unidadService.crear(new UnidadRequest(102, sectorId, propietarioId)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Ya existe una unidad con en numero 102");
  }

  @Test
  void crearUnidad_sectorInexistente_lanzaExcepcion() {
    assertThatThrownBy(() -> unidadService.crear(new UnidadRequest(103, 999L, propietarioId)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Sector no encontrado: 999");
  }

  @Test
  void crearUnidad_propietarioInexistente_lanzaExcepcion() {
    assertThatThrownBy(() -> unidadService.crear(new UnidadRequest(104, sectorId, 999L)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Usuario no encontrado: 999");
  }

  @Test
  void buscarPorId_existente_retornaUnidad() {
    UnidadResponse creada = unidadService.crear(new UnidadRequest(105, sectorId, propietarioId));

    UnidadResponse encontrada = unidadService.buscarPorId(creada.id());

    assertThat(encontrada.id()).isEqualTo(creada.id());
    assertThat(encontrada.numero()).isEqualTo(105);
  }

  @Test
  void buscarPorId_inexistente_lanzaExcepcion() {
    assertThatThrownBy(() -> unidadService.buscarPorId(999L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unidad no encontrada: 999");
  }

  @Test
  void listarPorSector_retornaUnidadesDelSector() {
    unidadService.crear(new UnidadRequest(106, sectorId, propietarioId));
    unidadService.crear(new UnidadRequest(107, sectorId, propietarioId));

    assertThat(unidadService.listarPorSector(sectorId)).hasSize(2);
  }

  @Test
  void listarPorSector_otroSector_noRetornaMezcla() {
    SectorResponse otroSector = sectorService.crear(new SectorRequest(11, "Otro Sector"));
    unidadService.crear(new UnidadRequest(108, sectorId, propietarioId));
    unidadService.crear(new UnidadRequest(109, otroSector.id(), propietarioId));

    assertThat(unidadService.listarPorSector(sectorId)).hasSize(1);
    assertThat(unidadService.listarPorSector(otroSector.id())).hasSize(1);
  }

  @Test
  void asignarResidente_exitoso() {
    UnidadResponse unidad = unidadService.crear(new UnidadRequest(110, sectorId, propietarioId));

    Usuario residente = usuarioRepository.save(Usuario.builder()
        .nombre("María Residente")
        .email("maria@email.com")
        .password("pass")
        .role(Role.RESIDENTE)
        .build());

    LocalDate inicio = LocalDate.of(2025, 1, 1);
    UnidadResponse actualizada = unidadService.asignarResidente(
        unidad.id(), new AsignarResidenteRequest(residente.getId(), inicio));

    assertThat(actualizada.residenteActualId()).isEqualTo(residente.getId());
    assertThat(actualizada.residenteActualNombre()).isEqualTo("María Residente");
  }

  @Test
  void asignarResidente_creaRegistroEnHistorial() {
    UnidadResponse unidad = unidadService.crear(new UnidadRequest(111, sectorId, propietarioId));

    Usuario residente = usuarioRepository.save(Usuario.builder()
        .nombre("Luis Residente")
        .email("luis@email.com")
        .password("pass")
        .role(Role.RESIDENTE)
        .build());

    LocalDate inicio = LocalDate.of(2025, 3, 1);
    unidadService.asignarResidente(unidad.id(), new AsignarResidenteRequest(residente.getId(), inicio));

    var historial = historialRepository.findByUnidadIdOrderByFechaInicioDesc(unidad.id());
    assertThat(historial).hasSize(1);
    assertThat(historial.get(0).getFechaFin()).isNull();
    assertThat(historial.get(0).getFechaInicio()).isEqualTo(inicio);
  }

  @Test
  void asignarResidente_cierraHistorialAnterior() {
    UnidadResponse unidad = unidadService.crear(new UnidadRequest(112, sectorId, propietarioId));

    Usuario residente1 = usuarioRepository.save(Usuario.builder()
        .nombre("Primer Residente")
        .email("primero@email.com")
        .password("pass")
        .role(Role.RESIDENTE)
        .build());

    Usuario residente2 = usuarioRepository.save(Usuario.builder()
        .nombre("Segundo Residente")
        .email("segundo@email.com")
        .password("pass")
        .role(Role.RESIDENTE)
        .build());

    LocalDate inicio1 = LocalDate.of(2024, 1, 1);
    LocalDate inicio2 = LocalDate.of(2025, 1, 1);

    unidadService.asignarResidente(unidad.id(), new AsignarResidenteRequest(residente1.getId(), inicio1));
    unidadService.asignarResidente(unidad.id(), new AsignarResidenteRequest(residente2.getId(), inicio2));

    var historial = historialRepository.findByUnidadIdOrderByFechaInicioDesc(unidad.id());

    assertThat(historial).hasSize(2);

    var registroAnterior = historial.stream()
        .filter(h -> h.getFechaFin() != null)
        .findFirst();

    assertThat(registroAnterior).isPresent();
    assertThat(registroAnterior.get().getFechaFin()).isEqualTo(inicio2.minusDays(1));

    var registroActual = historialRepository.findByUnidadIdAndFechaFinIsNull(unidad.id());
    assertThat(registroActual).isPresent();
    assertThat(registroActual.get().getUsuario().getId()).isEqualTo(residente2.getId());
  }

  @Test
  void actualizarPropietario_exitoso() {
    UnidadResponse unidad = unidadService.crear(new UnidadRequest(113, sectorId, propietarioId));

    Usuario nuevoPropietario = usuarioRepository.save(Usuario.builder()
        .nombre("Nuevo Propietario")
        .email("nuevoprop@email.com")
        .password("pass")
        .role(Role.RESIDENTE)
        .build());

    UnidadResponse actualizada = unidadService.actualizarPropietario(unidad.id(), nuevoPropietario.getId());

    assertThat(actualizada.propietarioId()).isEqualTo(nuevoPropietario.getId());
    assertThat(actualizada.propietarioNombre()).isEqualTo("Nuevo Propietario");
  }

  @Test
  void actualizarPropietario_usuarioInexistente_lanzaExcepcion() {
    UnidadResponse unidad = unidadService.crear(new UnidadRequest(114, sectorId, propietarioId));

    assertThatThrownBy(() -> unidadService.actualizarPropietario(unidad.id(), 999L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Usuario no encontrado: 999");
  }

}
