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
@Table(name = "sectores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sector {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false, unique = true)
  private Integer numero;

  @Column
  private String nombre;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delegado_id")
  private Usuario delegado;
}
