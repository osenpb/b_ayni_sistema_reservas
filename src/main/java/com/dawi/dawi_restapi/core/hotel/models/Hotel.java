package com.dawi.dawi_restapi.core.hotel.models;


import com.dawi.dawi_restapi.core.habitacion.models.Habitacion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "departamento_id")
    @JsonIgnore
    private Departamento departamento;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<Habitacion> habitaciones;

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

}
