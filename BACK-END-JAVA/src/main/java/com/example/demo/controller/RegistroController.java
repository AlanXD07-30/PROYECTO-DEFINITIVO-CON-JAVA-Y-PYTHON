package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.model.Usuario;
import com.example.demo.model.Rol;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Controller
@CrossOrigin(origins = "*")
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/registrar")
    @ResponseBody
    @Transactional
    public String registrarTodo(
            @RequestParam String email,
            @RequestParam String contrasena,
            @RequestParam String nombre,
            @RequestParam String identificacion,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String fecha_nacimiento) {

        try {
            // 1️⃣ Verificar si el email ya existe
            if (usuarioRepository.buscarPorEmail(email) != null) {
                return "Error: Ya existe un usuario con ese correo";
            }

            // 2️⃣ Crear Usuario
            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setContrasena(passwordEncoder.encode(contrasena));

            // 3️⃣ Asignar rol CLIENTE automáticamente
            Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                    .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));
            usuario.setRoles(Set.of(rolCliente));

            // Guardamos usuario primero para obtener ID
            Usuario usuarioGuardado = usuarioRepository.saveAndFlush(usuario);

            // 4️⃣ Crear Cliente
            Cliente cliente = new Cliente();
            cliente.setNombre(nombre);
            cliente.setIdentificacion(identificacion);
            cliente.setTelefono(telefono);
            cliente.setDireccion(direccion);

            if (fecha_nacimiento != null && !fecha_nacimiento.isEmpty()) {
                // Formato dd/MM/yyyy
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                cliente.setFechaNacimiento(LocalDate.parse(fecha_nacimiento, formatter));
            }

            cliente.setUsuario(usuarioGuardado); // vincula usuario al cliente
            clienteRepository.save(cliente);

            return "¡Te has registrado correctamente!";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al registrar: " + e.getMessage();
        }
    }
}