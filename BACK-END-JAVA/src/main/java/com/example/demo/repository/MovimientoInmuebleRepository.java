package com.example.demo.repository;

import com.example.demo.model.MovimientoInmueble;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientoInmuebleRepository extends JpaRepository<MovimientoInmueble, Long> {

    List<MovimientoInmueble> findByInmuebleId(Long idInmueble);
}
