package com.example.demo;

import com.example.demo.model.Rol;
import com.example.demo.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * Inicializador que asegura que exista el rol CLIENTE en la base de datos.
     * Evita que el registro falle por "Rol CLIENTE no encontrado".
     */
    @Bean
    public CommandLineRunner ensureRoles(RolRepository rolRepository) {
        return args -> {
            rolRepository.findByNombreRol("CLIENTE")
                    .orElseGet(() -> {
                        Rol r = new Rol("CLIENTE");
                        return rolRepository.save(r);
                    });
        };
    }
}
