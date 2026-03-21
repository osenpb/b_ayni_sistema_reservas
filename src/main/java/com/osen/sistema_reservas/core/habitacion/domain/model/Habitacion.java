package com.osen.sistema_reservas.core.habitacion.domain.model;

import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.osen.sistema_reservas.core.detalle_reserva.domain.model.DetalleReserva;
import com.osen.sistema_reservas.core.tipoHabitacion.domain.model.TipoHabitacion;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "habitacion", indexes = {
    @Index(name = "idx_habitacion_hotel", columnList = "hotel_id"),
    @Index(name = "idx_habitacion_tipo", columnList = "tipo_id")
})
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private double precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoHabitacion tipoHabitacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public TipoHabitacion getTipoHabitacion() { return tipoHabitacion; }
    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) { this.tipoHabitacion = tipoHabitacion; }
    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }
    public List<DetalleReserva> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleReserva> detalles) { this.detalles = detalles; }

    public static class HabitacionBuilder {
        private Long id;
        private String numero;
        private String estado;
        private double precio;
        private TipoHabitacion tipoHabitacion;
        private Hotel hotel;
        private List<DetalleReserva> detalles;

        public HabitacionBuilder id(Long id) { this.id = id; return this; }
        public HabitacionBuilder numero(String numero) { this.numero = numero; return this; }
        public HabitacionBuilder estado(String estado) { this.estado = estado; return this; }
        public HabitacionBuilder precio(double precio) { this.precio = precio; return this; }
        public HabitacionBuilder tipoHabitacion(TipoHabitacion tipoHabitacion) { this.tipoHabitacion = tipoHabitacion; return this; }
        public HabitacionBuilder hotel(Hotel hotel) { this.hotel = hotel; return this; }
        public HabitacionBuilder detalles(List<DetalleReserva> detalles) { this.detalles = detalles; return this; }

        public Habitacion build() {
            return new Habitacion(id, numero, estado, precio, tipoHabitacion, hotel, detalles);
        }
    }

    public static HabitacionBuilder builder() {
        return new HabitacionBuilder();
    }
}
