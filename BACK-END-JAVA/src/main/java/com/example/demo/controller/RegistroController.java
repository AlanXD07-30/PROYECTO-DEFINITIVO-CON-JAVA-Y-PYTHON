package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.model.Usuario;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping
public class RegistroController {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(RegistroController.class);

    public RegistroController(UsuarioRepository usuarioRepository,
                              ClienteRepository clienteRepository,
                              BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/registrar")
    @Transactional
    public ResponseEntity<String> registrar(
            @RequestParam String email,
            @RequestParam String contrasena,
            @RequestParam String nombre,
            @RequestParam String identificacion,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String fecha_nacimiento) {

        try {
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body("El email es obligatorio");
            }
            if (contrasena == null || contrasena.length() < 6) {
                return ResponseEntity.badRequest().body("La contraseña debe tener al menos 6 caracteres");
            }
            if (nombre == null || nombre.isBlank() || identificacion == null || identificacion.isBlank()) {
                return ResponseEntity.badRequest().body("Nombre e identificación son obligatorios");
            }

            if (usuarioRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un usuario con ese correo");
            }

            Usuario u = new Usuario();
            u.setEmail(email);
            u.setContrasena(passwordEncoder.encode(contrasena));
            u.setEstado("ACTIVO");
            usuarioRepository.save(u);

            LocalDate fecha = null;
            if (fecha_nacimiento != null && !fecha_nacimiento.isBlank()) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                try {
                    fecha = LocalDate.parse(fecha_nacimiento, fmt);
                } catch (DateTimeParseException ex) {
                    logger.warn("Formato de fecha inválido recibido: {}", fecha_nacimiento);
                    return ResponseEntity.badRequest().body("Formato de fecha inválido. Use dd/MM/yyyy");
                }
            }

            Cliente c = new Cliente();
            c.setNombre(nombre);
            c.setIdentificacion(identificacion);
            c.setTelefono(telefono);
            c.setDireccion(direccion);
            c.setFechaNacimiento(fecha);
            c.setUsuario(u);
            clienteRepository.save(c);

            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario y cliente registrados correctamente");
        } catch (Exception ex) {
            logger.error("Error registrando usuario/cliente", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar usuario/cliente");
        }
    }

    @GetMapping("/registrar")
    public ResponseEntity<String> registrarGet() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body("Este endpoint acepta solo POST. Use el formulario de registro.");
    }
}
