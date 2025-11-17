package com.dawi.dawi_restapi.core.hotel.repositories;

import com.dawi.dawi_restapi.core.hotel.models.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    Optional<Departamento> findByNombre(String nombre);
}