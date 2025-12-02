package com.dawi.dawi_restapi.helpers.mappers;

import com.dawi.dawi_restapi.core.cliente.dtos.ClienteRequest;
import com.dawi.dawi_restapi.core.cliente.dtos.ClienteResponse;
import com.dawi.dawi_restapi.core.cliente.model.Cliente;

public class ClienteMapper {

    public static ClienteResponse toResponse(Cliente cliente) {
        // ClienteResponse: (id, nombre, apellido, email, telefono, documento)
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getDni()
        );
    }

    public static Cliente toCliente(ClienteRequest clienteRequest) {
        Cliente cliente = new Cliente();
        cliente.setId(null);
        cliente.setDni(clienteRequest.dni());
        cliente.setNombre(clienteRequest.nombre());
        cliente.setApellido(clienteRequest.apellido());
        cliente.setEmail(clienteRequest.email());
        cliente.setTelefono(clienteRequest.telefono());
        return cliente;
    }


}
