package com.osen.sistema_reservas.core.hotel.model;


import com.osen.sistema_reservas.core.departamento.model.Departamento;
import com.osen.sistema_reservas.core.habitacion.models.Habitacion;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "departamento_id")

    private Departamento departamento;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Habitacion> habitaciones;

    private String imagenUrl;

    public double getPrecioMinimo() {
        if (habitaciones == null || habitaciones.isEmpty()) {
            return 0.0;
        }

        return habitaciones.stream()
                .mapToDouble(Habitacion::getPrecio)
                .min()
                .orElse(0.0);
    }

    public int cantidadHabitaciones() {
        return habitaciones.size();
    }

    public Hotel() {
    }

    public Hotel(Long id, String nombre, String direccion, Departamento departamento, List<Habitacion> habitaciones, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.departamento = departamento;
        this.habitaciones = habitaciones;
        this.imagenUrl = imagenUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public List<Habitacion> getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(List<Habitacion> habitaciones) {
        this.habitaciones = habitaciones;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
