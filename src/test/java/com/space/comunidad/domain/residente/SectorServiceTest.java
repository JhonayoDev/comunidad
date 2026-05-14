package com.space.comunidad.domain.residente;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.space.comunidad.TestcontainersConfiguration;
import com.space.comunidad.domain.residente.dto.SectorRequest;
import com.space.comunidad.domain.residente.dto.SectorResponse;
import com.space.comunidad.domain.residente.service.SectorService;
import com.space.comunidad.domain.user.entity.Role;
import com.space.comunidad.domain.user.entity.Usuario;
import com.space.comunidad.domain.user.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class SectorServiceTest {

  @Autowired
  SectorService sectorService;
  @Autowired
  UsuarioRepository usuarioRepository;

  @Test
  void crearSector_exitoso() {

    SectorResponse response = sectorService.crear(new SectorRequest(1, "norte"));

    assertThat(response.id()).isNotNull();
    assertThat(response.numero()).isEqualTo(1);
    assertThat(response.nombre()).isEqualTo("norte");
  }

  @Test
  void crearSector_sinNombre_exitoso() {
    SectorResponse response = sectorService.crear(new SectorRequest(2, null));

    assertThat(response.id()).isNotNull();
    assertThat(response.nombre()).isNull();
  }

  @Test
  void crearSector_numeroDuplicado_lanzaExcepcion() {
    SectorResponse response = sectorService.crear(new SectorRequest(2, "Norte"));

    assertThat(response.id()).isNotNull();

    assertThatThrownBy(() -> sectorService.crear(new SectorRequest(2, "Otro")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Ya existe un sector con el numero");
  }

  @Test
  void buscarPorId_existente_retornaSector() {
    SectorResponse creado = sectorService.crear(new SectorRequest(4, "Este"));

    SectorResponse encontrado = sectorService.buscarPorId(creado.id());

    assertThat(encontrado.id()).isEqualTo(creado.id());
    assertThat(encontrado.numero()).isEqualTo(4);
  }

  @Test
  void buscarPorId_inexistente_lanzaExcepcion() {
    assertThatThrownBy(() -> sectorService.buscarPorId(999L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Sector no encontrado: 999");
  }

  @Test
  void listarTodos_retornaListaCorrecta() {
    sectorService.crear(new SectorRequest(5, "Oeste"));
    sectorService.crear(
        new SectorRequest(6, "Norte-2"));

    assertThat(sectorService.listarTodos()).hasSizeGreaterThanOrEqualTo(2);
  }

  @Test
  void asignarDelegado_exitoso() {
    SectorResponse sector = sectorService.crear(new SectorRequest(2, "Sur"));

    Usuario usuario = usuarioRepository.save(Usuario.builder()
        .nombre("Juan Delegado")
        .email("delegado@email.com")
        .password("pass")
        .role(Role.RESIDENTE)
        .build());

    SectorResponse actualizado = sectorService.asignarDelegado(sector.id(), usuario.getId());

    assertThat(actualizado.delegadoId()).isEqualTo(usuario.getId());
    assertThat(actualizado.delegadoNombre()).isEqualTo(usuario.getNombre());
  }

  @Test
  void asignarDelegado_usuarioInexistente_lanzaExcepcion() {
    SectorResponse sector = sectorService.crear(new SectorRequest(8, "Este-2"));

    assertThatThrownBy(() -> sectorService.asignarDelegado(sector.id(), 999L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Usuario no encontrado con id: 999");
  }

  @Test
  void removerDelegado_exitoso() {
    SectorResponse response = sectorService.crear(new SectorRequest(1, "norte"));

    Usuario usuario = usuarioRepository.save(Usuario.builder()
        .nombre("Juan Delegado")
        .email("juan@email.com")
        .password("pass")
        .role(Role.RESIDENTE)
        .build());

    sectorService.asignarDelegado(response.id(), usuario.getId());
    SectorResponse sinDelegado = sectorService.removerDelegado(response.id());

    assertThat(sinDelegado.delegadoId()).isNull();
    assertThat(sinDelegado.delegadoNombre()).isNull();

  }

}
