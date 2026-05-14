package com.space.comunidad.domain.residente.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.space.comunidad.domain.residente.entity.Unidad;

public interface UnidadRepository extends JpaRepository<Unidad, Long> {

  Optional<Unidad> findByNumero(Integer numero);

  boolean existsByNumero(Integer numero);

  List<Unidad> findBySectorId(Long sectorId);

}
