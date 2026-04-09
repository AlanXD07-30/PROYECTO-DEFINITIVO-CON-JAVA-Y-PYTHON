package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InmuebleDTO {

    private String direccion;
    private String barrio;
    private String ciudad;
    private BigDecimal precio;
    private String estado;
    private BigDecimal metraje;
    private String tipoOperacion;
    private String tipoInmuebleNombre;
    private LocalDateTime fechaRegistro;
    private List<String> imagenes;

    public InmuebleDTO() {}

    // getters y setters

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getMetraje() { return metraje; }
    public void setMetraje(BigDecimal metraje) { this.metraje = metraje; }

    public String getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(String tipoOperacion) { this.tipoOperacion = tipoOperacion; }

    public String getTipoInmuebleNombre() { return tipoInmuebleNombre; }
    public void setTipoInmuebleNombre(String tipoInmuebleNombre) { this.tipoInmuebleNombre = tipoInmuebleNombre; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public List<String> getImagenes() { return imagenes; }
    public void setImagenes(List<String> imagenes) { this.imagenes = imagenes; }
}
