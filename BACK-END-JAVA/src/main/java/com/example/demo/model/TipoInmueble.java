package com.example.demo.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tipo_inmueble")
public class TipoInmueble implements Serializable {

    private static final long serialVersionUID = 1L;

    // Nombre de columna según tu BD: id_tipo (integer)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo")
    private Long id; // Long funciona con integer/bigint en la mayoría de setups

    @Column(name = "nombre_tipo", length = 255)
    private String nombreTipo;

    public TipoInmueble() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }

    @Override
    public String toString() {
        return "TipoInmueble{id=" + id + ", nombreTipo='" + nombreTipo + "'}";
    }
}
