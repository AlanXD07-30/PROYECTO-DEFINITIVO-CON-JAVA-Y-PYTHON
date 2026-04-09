package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // --- Consultas nativas originales (no tocar) ---
    @Query(value = """
        SELECT u.*
        FROM USUARIO u
        WHERE u.email = :email
    """, nativeQuery = true)
    Usuario buscarPorEmail(@Param("email") String email);

    @Query(value = """
        SELECT r.nombre_rol
        FROM ROL r
        JOIN USUARIO_ROL ur ON r.id_rol = ur.id_rol
        JOIN USUARIO u ON u.id_usuario = ur.id_usuario
        WHERE u.email = :email
    """, nativeQuery = true)
    List<String> obtenerRoles(@Param("email") String email);

    // --- Métodos nativos para registro (no tocar) ---
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO USUARIO (email, contrasena) VALUES (:email, :pass)", nativeQuery = true)
    void registrarUsuarioBase(@Param("email") String email, @Param("pass") String pass);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO USUARIO_ROL (id_usuario, id_rol) 
        VALUES ((SELECT id_usuario FROM USUARIO WHERE email = :email), 
                (SELECT id_rol FROM ROL WHERE nombre_rol = 'CLIENTE'))
    """, nativeQuery = true)
    void asignarRolCliente(@Param("email") String email);

    // --- Métodos recomendados y compatibles ---

    /**
     * Búsqueda segura por email (derivada por Spring Data).
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Compatibilidad: si otras clases llaman findByUsername(...) tratamos el argumento
     * como email y delegamos a findByEmail(...).
     */
    default Optional<Usuario> findByUsername(String username) {
        return findByEmail(username);
    }

    /**
     * Compatibilidad para findByUsernameOrEmail(username, email).
     */
    default Optional<Usuario> findByUsernameOrEmail(String username, String email) {
        Optional<Usuario> byUsername = findByEmail(username);
        if (byUsername.isPresent()) {
            return byUsername;
        }
        return findByEmail(email);
    }
}
