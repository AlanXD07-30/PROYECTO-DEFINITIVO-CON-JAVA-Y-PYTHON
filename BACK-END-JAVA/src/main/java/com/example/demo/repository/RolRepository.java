package com.example.demo.repository;

import com.example.demo.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Método recomendado: busca por el nombre del rol tal como está mapeado en la entidad (nombreRol).
     * Ejemplo de uso: rolRepository.findByNombreRol("CLIENTE")
     */
    Optional<Rol> findByNombreRol(String nombreRol);

    /**
     * Método de compatibilidad para código existente que llama a findByNombre(...)
     * Redirige internamente a findByNombreRol(...) para evitar cambios en controladores.
     */
    default Optional<Rol> findByNombre(String nombre) {
        return findByNombreRol(nombre);
    }

    /**
     * Variante insensible a mayúsculas/minúsculas (útil si los valores en BD pueden variar).
     * Puedes usarla si prefieres evitar problemas por casing.
     */
    Optional<Rol> findByNombreRolIgnoreCase(String nombreRol);
}
