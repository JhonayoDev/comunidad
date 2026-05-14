package com.space.comunidad.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.space.comunidad.domain.user.dto.AuthResponse;
import com.space.comunidad.domain.user.dto.LoginRequest;
import com.space.comunidad.domain.user.dto.RegisterRequest;
import com.space.comunidad.domain.user.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro e inicio de sesión de usuarios")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")

  @SecurityRequirements
  @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario en el sistema y retorna un token JWT.")
  @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente", content = @Content(schema = @Schema(implementation = AuthResponse.class)))
  @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
  @ApiResponse(responseCode = "500", description = "El email ya está registrado")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request));
  }

  @PostMapping
  @SecurityRequirements
  @Operation(summary = "Iniciar sesión", description = "Autentica un usuario con email y contraseña y retorna un token JWT.")
  @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(schema = @Schema(implementation = AuthResponse.class)))
  @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
  @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

}
