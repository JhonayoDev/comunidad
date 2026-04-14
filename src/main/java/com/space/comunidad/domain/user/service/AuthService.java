package com.space.comunidad.domain.user.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.space.comunidad.domain.user.dto.AuthResponse;
import com.space.comunidad.domain.user.dto.LoginRequest;
import com.space.comunidad.domain.user.dto.RegisterRequest;
import com.space.comunidad.domain.user.entity.Usuario;
import com.space.comunidad.domain.user.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthResponse register(RegisterRequest request) {
    if (usuarioRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("Email ya registrado");
    }
    Usuario usuario = Usuario.builder()
        .nombre(request.nombre())
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .role(request.role())
        .build();

    usuarioRepository.save(usuario);
    return new AuthResponse(jwtService.generateToken(usuario));
  }

  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.email(),
            request.password()));
    Usuario usuario = usuarioRepository.findByEmail(request.email())
        .orElseThrow();
    return new AuthResponse(jwtService.generateToken(usuario));
  }

}
