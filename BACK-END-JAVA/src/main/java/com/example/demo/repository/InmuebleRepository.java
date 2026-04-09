package com.example.demo.repository;

import com.example.demo.model.Inmueble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InmuebleRepository extends JpaRepository<Inmueble, Long> {

    // Este método trae exactamente lo que quieres ver en la página principal
    List<Inmueble> findByEstadoAndTipoOperacionIn(String estado, List<String> tipoOperacion);
}