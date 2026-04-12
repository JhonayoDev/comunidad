package com.space.comunidad.domain.user.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false)
  private String nombre;

  @NotBlank
  @Email
  @Column(nullable = false, unique = true)
  private String email;

  @NotBlank
  @Column(nullable = false)
  private String password;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Builder.Default
  @Column(nullable = false)
  private boolean activo = true;
}
