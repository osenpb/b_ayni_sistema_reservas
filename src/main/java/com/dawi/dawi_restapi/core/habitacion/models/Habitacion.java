package com.dawi.dawi_restapi.core.habitacion.models;


import com.dawi.dawi_restapi.core.hotel.models.Hotel;
import com.dawi.dawi_restapi.core.reserva.models.DetalleReserva;
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
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;
    private String estado; // DISPONIBLE, OCUPADA, MANTENIMIENTO
    private double precio;

    @ManyToOne
    @JoinColumn(name = "tipo_id")
    private TipoHabitacion tipoHabitacion;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL)
    private List<DetalleReserva> detalles;


}
