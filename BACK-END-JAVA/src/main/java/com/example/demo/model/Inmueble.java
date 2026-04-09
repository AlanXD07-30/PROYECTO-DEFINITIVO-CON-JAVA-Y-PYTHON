package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inmueble")
public class Inmueble implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inmueble")
    private Long id;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "barrio", length = 150)
    private String barrio;

    @Column(name = "ciudad", length = 150)
    private String ciudad;

    @Column(name = "precio")
    private Double precio;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "metraje")
    private Double metraje;

    // FK en la tabla: id_tipo (según tu esquema)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo", referencedColumnName = "id_tipo")
    private TipoInmueble tipoInmueble;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado_encargado")
    private Empleado empleadoEncargado;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "tipo_operacion", length = 50)
    private String tipoOperacion;

    @JsonIgnore
    @OneToMany(mappedBy = "inmueble", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ImagenInmueble> imagenes = new ArrayList<>();

    public Inmueble() {}

    // Getters y setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getMetraje() { return metraje; }
    public void setMetraje(Double metraje) { this.metraje = metraje; }

    public TipoInmueble getTipoInmueble() { return tipoInmueble; }
    public void setTipoInmueble(TipoInmueble tipoInmueble) { this.tipoInmueble = tipoInmueble; }

    public Empleado getEmpleadoEncargado() { return empleadoEncargado; }
    public void setEmpleadoEncargado(Empleado empleadoEncargado) { this.empleadoEncargado = empleadoEncargado; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(String tipoOperacion) { this.tipoOperacion = tipoOperacion; }

    public List<ImagenInmueble> getImagenes() { return imagenes; }
    public void setImagenes(List<ImagenInmueble> imagenes) { this.imagenes = imagenes; }

    public void addImagen(ImagenInmueble img) {
        imagenes.add(img);
        img.setInmueble(this);
    }

    public void removeImagen(ImagenInmueble img) {
        imagenes.remove(img);
        img.setInmueble(null);
    }

    @Override
    public String toString() {
        return "Inmueble{id=" + id + ", direccion='" + direccion + "', tipoOperacion='" + tipoOperacion + "'}";
    }
}
