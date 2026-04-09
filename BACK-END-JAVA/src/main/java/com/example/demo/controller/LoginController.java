package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    public LoginController(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Acepta POST en /perform_login (y /login por compatibilidad).
     * Devuelve JSON { "redirect": "..." } si la petición viene por fetch/ajax,
     * o una redirección HTTP si es submit normal.
     */
    @PostMapping(path = {"/perform_login", "/login"})
    public Object login(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request,
            @RequestHeader(value = "Accept", required = false) String acceptHeader,
            @RequestHeader(value = "X-Requested-With", required = false) String xRequestedWith
    ) {
        logger.info("Intento de login para email={}", email);

        try {
            if (email == null || email.isBlank() || password == null || password.isBlank()) {
                logger.warn("Credenciales incompletas recibidas para email={}", email);
                return handleResponse(request, acceptHeader, xRequestedWith, "http://127.0.0.1:5500/login.html?error=true");
            }

            Usuario usuario = usuarioRepository.buscarPorEmail(email);
            if (usuario == null) {
                logger.info("Usuario no encontrado para email={}", email);
                return handleResponse(request, acceptHeader, xRequestedWith, "http://127.0.0.1:5500/login.html?error=true");
            }

            String stored = usuario.getContrasena();
            if (stored == null || stored.isBlank()) {
                logger.error("Usuario encontrado sin contraseña en BD: email={}", email);
                return handleResponse(request, acceptHeader, xRequestedWith, "http://127.0.0.1:5500/login.html?error=true");
            }

            // Si usas NoOp, esta comparación es directa; si usas BCrypt, passwordEncoder.matches() funcionará.
            boolean passwordOk;
            try {
                passwordOk = passwordEncoder.matches(password, stored);
            } catch (Exception ex) {
                // En caso de que el encoder falle por formato, intentamos comparación directa como fallback.
                passwordOk = Objects.equals(password, stored);
            }

            if (!passwordOk) {
                logger.info("Contraseña inválida para email={}", email);
                return handleResponse(request, acceptHeader, xRequestedWith, "http://127.0.0.1:5500/login.html?error=true");
            }

            // Autenticación con AuthenticationManager
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(email, password);
            Authentication authenticated = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authenticated);
            logger.info("Autenticación exitosa para email={}", email);

            // Obtener roles desde la BD (tu método nativo)
            List<String> roles = usuarioRepository.obtenerRoles(email);
            if (roles == null) {
                logger.warn("No se encontraron roles para email={}", email);
                roles = List.of();
            }

            // Normalizar roles: quitar prefijo ROLE_ si existe y pasar a mayúsculas
            boolean isAdmin = roles.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                    .map(r -> r.toUpperCase(Locale.ROOT))
                    .anyMatch(r -> r.equals("ADMIN"));

            boolean isEmpleado = roles.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                    .map(r -> r.toUpperCase(Locale.ROOT))
                    .anyMatch(r -> r.equals("EMPLEADO"));

            boolean isSecretaria = roles.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                    .map(r -> r.toUpperCase(Locale.ROOT))
                    .anyMatch(r -> r.equals("SECRETARIA"));

            boolean isCliente = roles.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                    .map(r -> r.toUpperCase(Locale.ROOT))
                    .anyMatch(r -> r.equals("CLIENTE"));

            String redirectUrl;
            if (isAdmin) {
                redirectUrl = "http://127.0.0.1:5000/admin";
            } else if (isEmpleado) {
                redirectUrl = "http://127.0.0.1:5000/empleado";
            } else if (isSecretaria) {
                redirectUrl = "http://127.0.0.1:5000/secretaria";
            } else if (isCliente) {
                // Cliente no inicia sesión por ahora según tu requisito
                redirectUrl = "http://127.0.0.1:5500/login.html?error=registro_requerido";
            } else {
                logger.info("Usuario {} autenticado pero sin rol conocido: {}", email, roles);
                redirectUrl = "http://127.0.0.1:5500/login.html?error=no_autorizado";
            }

            return handleResponse(request, acceptHeader, xRequestedWith, redirectUrl);

        } catch (Exception ex) {
            logger.error("Error en login para email=" + email, ex);
            return handleResponse(request, acceptHeader, xRequestedWith, "http://127.0.0.1:5500/login.html?error=true");
        }
    }

    /**
     * Si la petición viene por fetch/ajax (X-Requested-With o Accept: application/json),
     * devolvemos JSON { redirect: url } con 200 OK.
     * Si es submit normal, devolvemos "redirect:..." para que Spring haga la redirección.
     */
    private Object handleResponse(HttpServletRequest request, String acceptHeader, String xRequestedWith, String redirectUrl) {
        boolean isAjax = false;

        if (xRequestedWith != null && "XMLHttpRequest".equalsIgnoreCase(xRequestedWith)) {
            isAjax = true;
        } else if (acceptHeader != null && acceptHeader.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE)) {
            isAjax = true;
        } else {
            // También consideramos peticiones fetch que envían header "Sec-Fetch-Mode: cors"
            String secFetchMode = request.getHeader("Sec-Fetch-Mode");
            if (secFetchMode != null && secFetchMode.equalsIgnoreCase("cors")) {
                isAjax = true;
            }
        }

        if (isAjax) {
            // Devolvemos JSON con la URL de redirección para que el frontend la procese
            return ResponseEntity.ok(Map.of("redirect", redirectUrl));
        } else {
            // Redirección clásica para submit normal
            return "redirect:" + redirectUrl;
        }
    }
}
