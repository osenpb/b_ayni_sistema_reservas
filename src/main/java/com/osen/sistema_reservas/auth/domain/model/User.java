package com.osen.sistema_reservas.auth.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_role", columnList = "role_id"),
    @Index(name = "idx_users_email", columnList = "email")
})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    private String telefono;

    @Column(nullable = false)
    private String nombre;

    private String apellido;

    @Column(unique = true)
    private String dni;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getRolename()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public User() {
    }

    public User(Long id, String username, String email, String password, Role role, String telefono, String nombre, String apellido, String dni, Boolean activo, LocalDateTime fechaCreacion) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.telefono = telefono;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }

    public void setId(Long id) {this.id = id;}

    public void setUsername(String username) {this.username = username;}

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }

    public void setRole(Role role) { this.role = role; }

    public String getTelefono() { return telefono; }

    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }

    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDni() { return dni; }

    public void setDni(String dni) { this.dni = dni; }

    public Boolean getActivo() { return activo; }

    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public static class UserBuilder {

        private Long id;
        private String username;
        private String email;
        private String password;
        private Role role;
        private String telefono;
        private String nombre;
        private String apellido;
        private String dni;
        private Boolean activo;
        private LocalDateTime fechaCreacion;

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserBuilder telefono(String telefono) {
            this.telefono = telefono;
            return this;
        }

        public UserBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public UserBuilder apellido(String apellido) {
            this.apellido = apellido;
            return this;
        }

        public UserBuilder dni(String dni) {
            this.dni = dni;
            return this;
        }

        public UserBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public UserBuilder fechaCreacion(LocalDateTime fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
            return this;
        }

        public User build() {
            return new User(
                    id,
                    username,
                    email,
                    password,
                    role,
                    telefono,
                    nombre,
                    apellido,
                    dni,
                    activo != null ? activo : true,
                    fechaCreacion != null ? fechaCreacion : LocalDateTime.now()
            );
        }
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

}
