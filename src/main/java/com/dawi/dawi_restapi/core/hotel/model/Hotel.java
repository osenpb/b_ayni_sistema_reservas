package com.dawi.dawi_restapi.core.hotel.model;


import com.dawi.dawi_restapi.core.departamento.model.Departamento;
import com.dawi.dawi_restapi.core.habitacion.models.Habitacion;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Builder
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

}
