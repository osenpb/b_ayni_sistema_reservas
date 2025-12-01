package com.dawi.dawi_restapi.core.reserva.models;

import com.dawi.dawi_restapi.core.cliente.model.Cliente;
import com.dawi.dawi_restapi.core.detalle_reserva.model.DetalleReserva;
import com.dawi.dawi_restapi.core.hotel.model.Hotel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaReserva;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double total;
    private String estado; // EJ: CONFIRMADA, CANCELADA

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleReserva> detalles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    //Se agrega esta funcion para controlar mejor  la persistencia bidireccional
    public void addDetalle(DetalleReserva detalle) {
        detalle.setReserva(this);
        this.detalles.add(detalle);
    }


}
