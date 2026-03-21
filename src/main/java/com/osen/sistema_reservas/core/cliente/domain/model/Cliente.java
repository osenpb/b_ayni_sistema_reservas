package com.osen.sistema_reservas.core.cliente.domain.model;

import com.osen.sistema_reservas.auth.domain.model.User;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "cliente", indexes = {
    @Index(name = "idx_cliente_dni", columnList = "dni"),
    @Index(name = "idx_cliente_user", columnList = "user_id"),
    @Index(name = "idx_cliente_email", columnList = "email")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_cliente_dni", columnNames = "dni"),
    @UniqueConstraint(name = "uk_cliente_email", columnNames = "email")
})
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String dni;

    private String telefono;

    @Column(unique = true)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Reserva> reservas;

    public Cliente() {
    }

    public Cliente(Long id, String nombre, String apellido, String dni, String telefono, String email, User user, List<Reserva> reservas) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.user = user;
        this.reservas = reservas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }
}
