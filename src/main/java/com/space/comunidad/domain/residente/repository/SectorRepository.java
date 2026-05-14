package com.space.comunidad.domain.residente.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.space.comunidad.domain.residente.entity.Sector;

public interface SectorRepository extends JpaRepository<Sector, Long> {

  Optional<Sector> findByNumero(Integer numero);

  boolean existsByNumero(Integer numero);

}
