package com.osen.sistema_reservas.core.detalle_reserva.domain.model;

import com.osen.sistema_reservas.core.habitacion.domain.model.Habitacion;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_reserva", indexes = {
    @Index(name = "idx_detalle_reserva", columnList = "reserva_id"),
    @Index(name = "idx_detalle_habitacion", columnList = "habitacion_id")
})
public class DetalleReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double precioNoche;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion;

    public DetalleReserva() { }

    public DetalleReserva(Long id, double precioNoche, Reserva reserva, Habitacion habitacion) {
        this.id = id;
        this.precioNoche = precioNoche;
        this.reserva = reserva;
        this.habitacion = habitacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public double getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(double precioNoche) { this.precioNoche = precioNoche; }
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public Habitacion getHabitacion() { return habitacion; }
    public void setHabitacion(Habitacion habitacion) { this.habitacion = habitacion; }
}
