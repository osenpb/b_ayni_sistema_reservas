package com.dawi.dawi_restapi.core.hotel.services;

import com.dawi.dawi_restapi.core.hotel.models.Departamento;
import com.dawi.dawi_restapi.core.hotel.repositories.DepartamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public Optional<Departamento> buscarPorId(Long id) {
        return departamentoRepository.findById(id);
    }

    public List<Departamento> listar(){
        return departamentoRepository.findAll();
    }

    public Departamento guardar(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }

    public void eliminar(Long depId) {
        departamentoRepository.deleteById(depId);
    }

    public Optional<Departamento> buscarPorNombre(String nombre) {
        return departamentoRepository.findByNombre(nombre);
    }

}
