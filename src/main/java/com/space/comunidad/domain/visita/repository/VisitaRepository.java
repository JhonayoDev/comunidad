package com.space.comunidad.domain.visita.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.space.comunidad.domain.visita.entity.CategoriaVisita;
import com.space.comunidad.domain.visita.entity.Visita;

import io.lettuce.core.dynamic.annotation.Param;

public interface VisitaRepository extends JpaRepository<Visita, Long> {

  List<Visita> findByPatenteContainingIgnoreCase(String patente);

  List<Visita> findByNombreResponsableContainingIgnoreCase(String nombre);

  List<Visita> findByHoraSAlidaIsNull();

  List<Visita> findByCategoria(CategoriaVisita categoria);

  @Query("""
      SELECT V FROM Visita v
      JOIN v.unidades vu
      WHERE vu.unidad.id = :unidadId
      ORDER BY v.horaIngreso DESC
      """)
  List<Visita> findByUnidadId(@Param("unidadId") Long unidadId);
}
