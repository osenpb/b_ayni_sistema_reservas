package com.dawi.dawi_restapi.core.cliente.repository;

import com.dawi.dawi_restapi.core.cliente.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDni(String dni);
}