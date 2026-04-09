package com.example.demo.repository;

import com.example.demo.model.ImagenInmueble;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ImagenInmuebleRepository extends JpaRepository<ImagenInmueble, Integer> {

    List<ImagenInmueble> findByInmuebleId(Long idInmueble);
}
