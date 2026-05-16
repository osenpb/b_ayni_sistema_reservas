package com.osen.sistema_reservas.core.habitacion.domain.model;

import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.osen.sistema_reservas.core.detalle_reserva.domain.model.DetalleReserva;
import com.osen.sistema_reservas.core.tipoHabitacion.domain.model.TipoHabitacion;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "habitacion", indexes = {
    @Index(name = "idx_habitacion_hotel", columnList = "hotel_id"),
    @Index(name = "idx_habitacion_tipo", columnList = "tipo_id")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoHabitacion estado;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoHabitacion tipoHabitacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL)
    private List<DetalleReserva> detalles;
}
