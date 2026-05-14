package com.space.comunidad.domain.residente.entity;

import com.space.comunidad.domain.user.entity.Usuario;

import jakarta.persistence.Column;
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
@Table(name = "unidades")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Unidad {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false, unique = true)
  private Integer numero;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "sector_id", nullable = false)
  private Sector sector;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "propietario_id", nullable = false)
  private Usuario propietario;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "residente_actual_id")
  private Usuario residenteActual;

}
