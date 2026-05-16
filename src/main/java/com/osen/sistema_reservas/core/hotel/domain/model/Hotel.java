package com.osen.sistema_reservas.core.hotel.domain.model;

import com.osen.sistema_reservas.core.departamento.domain.model.Departamento;
import com.osen.sistema_reservas.core.habitacion.domain.model.Habitacion;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "hotel", indexes = {
    @Index(name = "idx_hotel_departamento", columnList = "departamento_id")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String direccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Habitacion> habitaciones;

    private String imagenUrl;

    public BigDecimal getPrecioMinimo() {
        if (habitaciones == null || habitaciones.isEmpty()) return BigDecimal.ZERO;
        return habitaciones.stream()
                .map(Habitacion::getPrecio)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    public int cantidadHabitaciones() {
        return habitaciones != null ? habitaciones.size() : 0;
    }
}
