package com.space.comunidad.domain.visita.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.space.comunidad.domain.user.entity.Usuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "visitas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Visita {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(length = 7)
  private String patente;

  @Column(nullable = false)
  private String nombreResponsable;

  @Column(nullable = false)
  private Integer cantidadPersonas;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CategoriaVisita categoria;

  @Column
  private String descripcionCategoria;

  @Column(nullable = false)
  private LocalDateTime horaIngreso;

  @Column
  private LocalDateTime horaSalida;

  @Column(length = 500)
  private String notas;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "registrado_por_id", nullable = false)
  private Usuario registradoPor;

  @Builder.Default
  @OneToMany(mappedBy = "visita", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<VisitaUnidad> unidades = new ArrayList<>();

}
