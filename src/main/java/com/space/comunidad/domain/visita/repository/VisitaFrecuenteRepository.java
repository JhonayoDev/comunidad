package com.space.comunidad.domain.visita.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.space.comunidad.domain.visita.entity.VisitaFrecuente;

public interface VisitaFrecuenteRepository extends JpaRepository<VisitaFrecuente, String> {

  List<VisitaFrecuente> findByPatenteStartingWithIgnoreCase(String patente);

}
