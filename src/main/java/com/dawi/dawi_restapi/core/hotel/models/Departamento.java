package com.dawi.dawi_restapi.core.hotel.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String detalle;

    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Hotel> hoteles;


}
