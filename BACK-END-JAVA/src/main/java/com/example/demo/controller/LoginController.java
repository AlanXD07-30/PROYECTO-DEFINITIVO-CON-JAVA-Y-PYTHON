package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password) {

        Usuario usuario = usuarioRepository.buscarPorEmail(email);

        // Usamos .getContrasena() porque así está en tu tabla USUARIO
        if (usuario != null && usuario.getContrasena().equals(password)) {

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
        }

        return "redirect:http://127.0.0.1:5500/login.html?error=true";
    }
}