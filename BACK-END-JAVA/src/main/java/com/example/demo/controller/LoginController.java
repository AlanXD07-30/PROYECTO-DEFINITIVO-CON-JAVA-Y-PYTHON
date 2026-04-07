package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    public LoginController(UsuarioRepository usuarioRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password) {

        try {
            Usuario usuario = usuarioRepository.buscarPorEmail(email);
            if (usuario == null) {
                return "redirect:http://127.0.0.1:5500/login.html?error=true";
            }

            if (!passwordEncoder.matches(password, usuario.getContrasena())) {
                return "redirect:http://127.0.0.1:5500/login.html?error=true";
            }

            Authentication auth = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authenticated = authenticationManager.authenticate(auth);
            SecurityContextHolder.getContext().setAuthentication(authenticated);

            List<String> roles = usuarioRepository.obtenerRoles(email);

            if (roles.contains("ADMIN")) {
                return "redirect:http://127.0.0.1:5000/admin";
            }
            if (roles.contains("EMPLEADO")) {
                return "redirect:http://127.0.0.1:5000/empleado";
            }
            if (roles.contains("SECRETARIA")) {
                return "redirect:http://127.0.0.1:5000/secretaria";
            }
            if (roles.contains("CLIENTE")) {
                return "redirect:http://127.0.0.1:5500/login.html?error=registro_requerido";
            }

            return "redirect:http://127.0.0.1:5500/login.html?error=no_autorizado";
        } catch (Exception ex) {
            logger.error("Error en login", ex);
            return "redirect:http://127.0.0.1:5500/login.html?error=true";
        }
    }
}
