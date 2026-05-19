package com.space.comunidad.domain.visita.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "visitante_frecuente")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitaFrecuente {

  @Id
  @Column(length = 7, nullable = false)
  private String patente;

  @Column(nullable = false)
  private String nombreResponsable;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CategoriaVisita categoria;

  @Column
  private String descripcionCategoria;

  @Column(nullable = false)
  private LocalDateTime ultimaVisita;
}
