package com.space.comunidad.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.space.comunidad.domain.user.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UsuarioRepository usuarioRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return usuarioRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(
            "Usuario no encontrado con email: " + email));
  }

}
