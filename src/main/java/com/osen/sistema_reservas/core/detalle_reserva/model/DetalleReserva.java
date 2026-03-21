package com.osen.sistema_reservas.core.detalle_reserva.model;


import com.osen.sistema_reservas.core.habitacion.models.Habitacion;
import com.osen.sistema_reservas.core.reserva.models.Reserva;
import jakarta.persistence.*;
import lombok.*;


@Entity
public class DetalleReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double precioNoche;

    @ManyToOne
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name = "habitacion_id")
    private Habitacion habitacion;

    public DetalleReserva() {  }

    public DetalleReserva(Long id, double precioNoche, Reserva reserva, Habitacion habitacion) {
        this.id = id;
        this.precioNoche = precioNoche;
        this.reserva = reserva;
        this.habitacion = habitacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPrecioNoche() {
        return precioNoche;
    }

    public void setPrecioNoche(double precioNoche) {
        this.precioNoche = precioNoche;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }
}
