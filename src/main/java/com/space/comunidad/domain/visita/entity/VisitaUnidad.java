package com.space.comunidad.domain.visita.entity;

import com.space.comunidad.domain.residente.entity.Unidad;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "visita_unidades")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitaUnidad {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "visita_id", nullable = false)
  private Visita visita;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "unidad_id", nullable = false)
  private Unidad unidad;
}
