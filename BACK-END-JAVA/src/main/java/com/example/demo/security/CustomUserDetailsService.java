package com.example.demo.security;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            logger.warn("loadUserByUsername llamado con usernameOrEmail vacío");
            throw new UsernameNotFoundException("Nombre de usuario o email vacío");
        }

        Usuario usuario = usuarioRepository.buscarPorEmail(usernameOrEmail);
        if (usuario == null) {
            logger.warn("Usuario no encontrado para: {}", usernameOrEmail);
            throw new UsernameNotFoundException("Usuario no encontrado: " + usernameOrEmail);
        }

        String email = usuario.getEmail();
        String password = usuario.getContrasena();

        if (email == null || email.isBlank()) {
            logger.error("Usuario encontrado sin email: idUsuario={}", usuario.getIdUsuario());
            throw new UsernameNotFoundException("Usuario inválido (sin email)");
        }
        if (password == null || password.isBlank()) {
            logger.error("Usuario encontrado sin contraseña en BD: email={}", email);
            throw new UsernameNotFoundException("Usuario inválido (sin contraseña)");
        }

        List<String> roles = usuarioRepository.obtenerRoles(email);
        if (roles == null) {
            roles = Collections.emptyList();
        }

        List<GrantedAuthority> authorities = roles.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(r -> !r.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        logger.debug("Cargando UserDetails para {} con roles {}", email, roles);

        return User.builder()
                .username(email)
                .password(password)
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
