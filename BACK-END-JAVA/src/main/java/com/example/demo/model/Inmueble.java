package com.example.demo.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "inmueble")
public class Inmueble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inmueble")
    private Long id;

    private String direccion;
    private String barrio;
    private String ciudad;
    private double precio;
    private String estado;
    private double metraje;

   
    @Column(name = "tipo_operacion", nullable = false)
    private String tipoOperacion;

    @Column(name = "id_tipo")
    private Long idTipo;

    @Column(name = "id_empleado_encargado")
    private Long idEmpleadoEncargado;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_registro")
    private Date fechaRegistro;

    // GETTERS Y SETTERS

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getMetraje() { return metraje; }
    public void setMetraje(double metraje) { this.metraje = metraje; }

    public String getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(String tipoOperacion) { this.tipoOperacion = tipoOperacion; }

    public Long getIdTipo() { return idTipo; }
    public void setIdTipo(Long idTipo) { this.idTipo = idTipo; }

    public Long getIdEmpleadoEncargado() { return idEmpleadoEncargado; }
    public void setIdEmpleadoEncargado(Long idEmpleadoEncargado) { this.idEmpleadoEncargado = idEmpleadoEncargado; }

    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}