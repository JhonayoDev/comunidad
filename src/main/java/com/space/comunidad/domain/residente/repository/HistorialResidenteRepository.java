package com.space.comunidad.domain.residente.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.space.comunidad.domain.residente.entity.HistorialResidente;

public interface HistorialResidenteRepository extends JpaRepository<HistorialResidente, Long> {

  List<HistorialResidente> findByUnidadIdOrderByFechaInicioDesc(Long unidadId);

  Optional<HistorialResidente> findByUnidadIdAndFechaFinIsNull(Long unidadId);

}
