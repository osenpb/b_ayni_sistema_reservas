package com.dawi.dawi_restapi.core.cliente.service;

import com.dawi.dawi_restapi.core.cliente.dtos.ClienteRequest;
import com.dawi.dawi_restapi.core.cliente.model.Cliente;
import com.dawi.dawi_restapi.core.cliente.repository.ClienteRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorDni(String dni) {
        return clienteRepository.findByDni(dni).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

}