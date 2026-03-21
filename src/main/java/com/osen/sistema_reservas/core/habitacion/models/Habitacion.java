package com.osen.sistema_reservas.core.habitacion.models;


import com.osen.sistema_reservas.core.hotel.model.Hotel;
import com.osen.sistema_reservas.core.detalle_reserva.model.DetalleReserva;
import com.osen.sistema_reservas.core.tipoHabitacion.model.TipoHabitacion;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
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

    public Habitacion() {
    }

    public Habitacion(Long id, String numero, String estado, double precio, TipoHabitacion tipoHabitacion, Hotel hotel, List<DetalleReserva> detalles) {
        this.id = id;
        this.numero = numero;
        this.estado = estado;
        this.precio = precio;
        this.tipoHabitacion = tipoHabitacion;
        this.hotel = hotel;
        this.detalles = detalles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public TipoHabitacion getTipoHabitacion() {
        return tipoHabitacion;
    }

    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public List<DetalleReserva> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleReserva> detalles) {
        this.detalles = detalles;
    }

    public static class HabitacionBuilder {

        private Long id;
        private String numero;
        private String estado;
        private double precio;
        private TipoHabitacion tipoHabitacion;
        private Hotel hotel;
        private List<DetalleReserva> detalles;

        public HabitacionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public HabitacionBuilder numero(String numero) {
            this.numero = numero;
            return this;
        }

        public HabitacionBuilder estado(String estado) {
            this.estado = estado;
            return this;
        }

        public HabitacionBuilder precio(double precio) {
            this.precio = precio;
            return this;
        }

        public HabitacionBuilder tipoHabitacion(TipoHabitacion tipoHabitacion) {
            this.tipoHabitacion = tipoHabitacion;
            return this;
        }

        public HabitacionBuilder hotel(Hotel hotel) {
            this.hotel = hotel;
            return this;
        }

        public HabitacionBuilder detalles(List<DetalleReserva> detalles) {
            this.detalles = detalles;
            return this;
        }

        public Habitacion build() {
            return new Habitacion(
                    id,
                    numero,
                    estado,
                    precio,
                    tipoHabitacion,
                    hotel,
                    detalles
            );
        }
    }

    public static HabitacionBuilder builder() {
        return new HabitacionBuilder();
    }


}
