package com.osen.sistema_reservas.core.departamento.service;

import com.osen.sistema_reservas.core.departamento.dtos.DepartamentoRequest;
import com.osen.sistema_reservas.core.departamento.model.Departamento;
import com.osen.sistema_reservas.core.departamento.repository.DepartamentoRepository;
import com.osen.sistema_reservas.helpers.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    public List<Departamento> listar() {
        return departamentoRepository.findAll();
    }

    public Departamento buscarPorId(Long id) {
        return departamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Departamento " + id));
    }

    public Optional<Departamento> buscarPorIdOptional(Long id) {
        return departamentoRepository.findById(id);
    }

    public Optional<Departamento> buscarPorNombre(String nombre) {
        return departamentoRepository.findByNombre(nombre);
    }

    public Departamento guardar(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }

    public Departamento crear(DepartamentoRequest request) {
        Departamento departamento = new Departamento();
        departamento.setNombre(request.nombre());
        departamento.setDetalle(request.detalle());
        return departamentoRepository.save(departamento);
    }

    public Departamento actualizar(Long id, DepartamentoRequest request) {
        Departamento departamento = buscarPorId(id);
        departamento.setNombre(request.nombre());
        departamento.setDetalle(request.detalle());
        return departamentoRepository.save(departamento);
    }

    public void eliminar(Long depId) {
        if (!departamentoRepository.existsById(depId)) {
            throw new EntityNotFoundException("Departamento" + depId);
        }
        departamentoRepository.deleteById(depId);
    }
}
