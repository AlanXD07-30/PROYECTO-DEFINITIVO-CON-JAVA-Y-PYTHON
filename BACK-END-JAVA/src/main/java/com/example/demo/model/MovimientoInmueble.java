package com.example.demo.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "movimiento_inmueble")
public class MovimientoInmueble implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long id;

    @Column(name = "tipo_movimiento", length = 100)
    private String tipoMovimiento;

    // Si tu tabla no tiene fecha_movimiento, mantenemos transient para evitar error de validación.
    @Transient
    private java.time.LocalDateTime fechaMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inmueble")
    private Inmueble inmueble;

    public MovimientoInmueble() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public java.time.LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(java.time.LocalDateTime fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }

    public Inmueble getInmueble() { return inmueble; }
    public void setInmueble(Inmueble inmueble) { this.inmueble = inmueble; }
}
