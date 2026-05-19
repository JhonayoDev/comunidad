package com.space.comunidad.domain.visita;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.space.comunidad.TestcontainersConfiguration;
import com.space.comunidad.domain.residente.dto.SectorRequest;
import com.space.comunidad.domain.residente.dto.UnidadRequest;
import com.space.comunidad.domain.residente.service.SectorService;
import com.space.comunidad.domain.residente.service.UnidadService;
import com.space.comunidad.domain.user.entity.Role;
import com.space.comunidad.domain.user.entity.Usuario;
import com.space.comunidad.domain.user.repository.UsuarioRepository;
import com.space.comunidad.domain.visita.dto.RegistrarVisitaRequest;
import com.space.comunidad.domain.visita.dto.VisitaResponse;
import com.space.comunidad.domain.visita.entity.CategoriaVisita;
import com.space.comunidad.domain.visita.entity.VisitaFrecuente;
import com.space.comunidad.domain.visita.repository.VisitaFrecuenteRepository;
import com.space.comunidad.domain.visita.service.VisitaService;

import jakarta.transaction.Transactional;

@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class VisitaServiceTest {

  @Autowired
  VisitaService visitaService;
  @Autowired
  VisitaFrecuenteRepository visitaFrecuenteRepository;
  @Autowired
  UsuarioRepository usuarioRepository;
  @Autowired
  SectorService sectorService;
  @Autowired
  UnidadService unidadService;

  private Long guardiaId;
  private Long unidadId;
  private Long unidadId2;

  @BeforeEach
  void setup() {
    Usuario guardia = usuarioRepository.save(Usuario.builder()
        .nombre("Guardia test")
        .email("guardia@test.com")
        .password("test")
        .role(Role.GUARDIA)
        .build());
    guardiaId = guardia.getId();

    Usuario propietario = usuarioRepository.save(Usuario.builder()
        .nombre("Propietario test")
        .email("propietario@test.com")
        .password("pass")
        .role(Role.RESIDENTE)
        .build());
    var sector = sectorService.crear(new SectorRequest(1, "primero"));
    unidadId = unidadService.crear(new UnidadRequest(21, sector.id(), propietario.getId())).id();
    unidadId2 = unidadService.crear(new UnidadRequest(22, sector.id(), propietario.getId())).id();
  }

  @Test
  void registrarIngreso_aPie_exitoso() {
    RegistrarVisitaRequest request = new RegistrarVisitaRequest(
        null,
        "Pedro Visita",
        1,
        CategoriaVisita.VISITA_PERSONAL,
        null, List.of(unidadId),
        null);
    VisitaResponse response = visitaService.registrarIngreso(request, guardiaId);

    assertThat(response.id()).isNotNull();
    assertThat(response.patente()).isNull();
    assertThat(response.horaIngreso()).isNotNull();
    assertThat(response.horaSalida()).isNull();
    assertThat(response.unidades()).hasSize(1);

  }

  @Test
  void registrarIngreso_conVehiculo_exitoso() {
    RegistrarVisitaRequest request = new RegistrarVisitaRequest(
        "abc123",
        "Juan conductor",
        2,
        CategoriaVisita.DELIVERY,
        null,
        List.of(unidadId),
        "Deja encomienda");

    VisitaResponse response = visitaService.registrarIngreso(request, guardiaId);

    assertThat(response.patente()).isEqualTo("ABC123");
    assertThat(response.cantidadPersonas()).isEqualTo(2);
    assertThat(response.notas()).isEqualTo("Deja encomienda");
  }

  @Test
  void registrarIngreso_variasUnidades_exitoso() {
    RegistrarVisitaRequest request = new RegistrarVisitaRequest(
        "xyz999", "Repartidor Gas", 1, CategoriaVisita.COMERCIO_AMBULANTE, null, List.of(unidadId, unidadId2), null);

    VisitaResponse response = visitaService.registrarIngreso(request, guardiaId);

    assertThat(response.patente()).isEqualTo("XYZ999");
    assertThat(response.unidades()).hasSize(2);
  }

  @Test
  void registrarIngreso_categoriaOtro_sinDescipcion_lanzaExepcion() {
    RegistrarVisitaRequest request = new RegistrarVisitaRequest(
        null, "Visita especial", 1, CategoriaVisita.OTRO, null, List.of(unidadId), null);

    assertThatThrownBy(() -> visitaService.registrarIngreso(request, guardiaId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("categoria es obligatoria");
  }

  @Test
  void registrarIngreso_categoriaOtro_conDescripcion_exitoso() {
    var request = new RegistrarVisitaRequest(
        null, "Inspector Municipal", 1, CategoriaVisita.OTRO,
        "Fiscalización municipal", List.of(unidadId), null);

    VisitaResponse response = visitaService.registrarIngreso(request, guardiaId);

    assertThat(response.descripcionCategoria()).isEqualTo("Fiscalización municipal");
  }

  @Test
  void registrarIngreso_unidadInexistente_lanzaExcepcion() {
    var request = new RegistrarVisitaRequest(
        null, "Visita", 1, CategoriaVisita.VISITA_PERSONAL,
        null, List.of(999L), null);

    assertThatThrownBy(() -> visitaService.registrarIngreso(request, guardiaId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("unidades no fueron encontradas");
  }

  // TODO
  // visitante frecuente
  @Test
  void registrarIngreso_conPatente_creaVisitanteFrecuente() {
    var request = new RegistrarVisitaRequest(
        "DEF456", "Carlos Frecuente", 1, CategoriaVisita.DELIVERY,
        null, List.of(unidadId), null);

    visitaService.registrarIngreso(request, guardiaId);

    var frecuente = visitaFrecuenteRepository.findById("DEF456");
    assertThat(frecuente).isPresent();
    assertThat(frecuente.get().getNombreResponsable()).isEqualTo("Carlos Frecuente");
  }

  @Test
  void registrarIngreso_patenteRepetida_actualizaVisitanteFrecuente() {
    visitaService.registrarIngreso(new RegistrarVisitaRequest(
        "GHI789", "Nombre Viejo", 1, CategoriaVisita.DELIVERY,
        null, List.of(unidadId), null), guardiaId);

    visitaService.registrarIngreso(new RegistrarVisitaRequest(
        "GHI789", "Nombre Actualizado", 1, CategoriaVisita.SERVICIO_HOGAR,
        null, List.of(unidadId), null), guardiaId);

    var frecuente = visitaFrecuenteRepository.findById("GHI789");
    assertThat(frecuente.get().getNombreResponsable()).isEqualTo("Nombre Actualizado");
    assertThat(frecuente.get().getCategoria()).isEqualTo(CategoriaVisita.SERVICIO_HOGAR);
  }

  @Test
  void registrarIngreso_sinPatente_noActualizaFrecuentes() {
    visitaService.registrarIngreso(new RegistrarVisitaRequest(
        null, "Visita A Pie", 1, CategoriaVisita.VISITA_PERSONAL,
        null, List.of(unidadId), null), guardiaId);

    assertThat(visitaFrecuenteRepository.findAll()).isEmpty();
  }

  @Test
  void sugerenciasPorPatente_retornaCoincidencias() {
    visitaService.registrarIngreso(new RegistrarVisitaRequest(
        "AAA111", "Primero", 1, CategoriaVisita.DELIVERY,
        null, List.of(unidadId), null), guardiaId);

    visitaService.registrarIngreso(new RegistrarVisitaRequest(
        "AAA222", "Segundo", 1, CategoriaVisita.DELIVERY,
        null, List.of(unidadId), null), guardiaId);

    visitaService.registrarIngreso(new RegistrarVisitaRequest(
        "BBB333", "Otro", 1, CategoriaVisita.DELIVERY,
        null, List.of(unidadId), null), guardiaId);

    var sugerencias = visitaService.sugerenciasPorPatente("AAA");

    assertThat(sugerencias).hasSize(2);
    assertThat(sugerencias).extracting("patente").containsExactlyInAnyOrder("AAA111", "AAA222");
  }

  // registrar salida

  @Test
  void registrarSalida_exitoso() {
    VisitaResponse visita = visitaService.registrarIngreso(new RegistrarVisitaRequest(
        null, "Visita Salida", 1, CategoriaVisita.VISITA_PERSONAL,
        null, List.of(unidadId), null), guardiaId);

    VisitaResponse conSalida = visitaService.registrarSalida(visita.id());

    assertThat(conSalida.horaSalida()).isNotNull();
  }

  @Test
  void registrarSalida_duplicada_lanzaExcepcion() {
    VisitaResponse visita = visitaService.registrarIngreso(new RegistrarVisitaRequest(
        null, "Visita Doble Salida", 1, CategoriaVisita.VISITA_PERSONAL,
        null, List.of(unidadId), null), guardiaId);

    visitaService.registrarSalida(visita.id());

    assertThatThrownBy(() -> visitaService.registrarSalida(visita.id()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("ya tiene hora de salida");
  }

  // consultas

  @Test
  void buscarPorPatente_parcial_retornaResultados() {
    visitaService.registrarIngreso(new RegistrarVisitaRequest(
        "ZZZ001", "Buscable", 1, CategoriaVisita.DELIVERY,
        null, List.of(unidadId), null), guardiaId);

    var resultados = visitaService.buscarPorPatente("ZZZ");

    assertThat(resultados).hasSize(1);
    assertThat(resultados.get(0).patente()).isEqualTo("ZZZ001");
  }

  @Test
  void listarActivas_noIncluye_visitas_con_salida() {
    VisitaResponse v1 = visitaService.registrarIngreso(new RegistrarVisitaRequest(
        null, "Activa", 1, CategoriaVisita.VISITA_PERSONAL,
        null, List.of(unidadId), null), guardiaId);

    VisitaResponse v2 = visitaService.registrarIngreso(new RegistrarVisitaRequest(
        null, "Cerrada", 1, CategoriaVisita.VISITA_PERSONAL,
        null, List.of(unidadId), null), guardiaId);

    visitaService.registrarSalida(v2.id());

    var activas = visitaService.listarActivas();

    assertThat(activas).extracting("id").contains(v1.id());
    assertThat(activas).extracting("id").doesNotContain(v2.id());
  }
}
