package com.osen.sistema_reservas.core.departamento.domain.model;

import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String detalle;

    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Hotel> hoteles;

    public Departamento() {
    }

    public Departamento(Long id, String nombre, String detalle, List<Hotel> hoteles) {
        this.id = id;
        this.nombre = nombre;
        this.detalle = detalle;
        this.hoteles = hoteles;
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

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public List<Hotel> getHoteles() {
        return hoteles;
    }

    public void setHoteles(List<Hotel> hoteles) {
        this.hoteles = hoteles;
    }
}
