package com.osen.sistema_reservas.core.departamento.application.service;

import com.osen.sistema_reservas.core.departamento.application.dtos.DepartamentoRequest;
import com.osen.sistema_reservas.core.departamento.application.dtos.DepartamentoResponse;
import com.osen.sistema_reservas.core.departamento.domain.model.Departamento;
import com.osen.sistema_reservas.core.departamento.domain.port.out.DepartamentoRepository;
import com.osen.sistema_reservas.shared.helpers.exceptions.EntityNotFoundException;
import com.osen.sistema_reservas.shared.helpers.mappers.DepartamentoMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "departamentos", key = "'all'")
    public List<Departamento> listar() {
        return departamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "departamentos", key = "'all_dto'")
    public List<DepartamentoResponse> listarResponse() {
        return departamentoRepository.findAll().stream()
                .map(DepartamentoMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<DepartamentoResponse> buscarPorNombreResponse(String nombre) {
        return departamentoRepository.findByNombre(nombre)
                .map(DepartamentoMapper::toDTO);
    }

    @Transactional(readOnly = true)
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

    @CacheEvict(value = "departamentos", allEntries = true)
    public Departamento guardar(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }

    @CacheEvict(value = "departamentos", allEntries = true)
    public Departamento crear(DepartamentoRequest request) {
        Departamento departamento = new Departamento();
        departamento.setNombre(request.nombre());
        departamento.setDetalle(request.detalle());
        return departamentoRepository.save(departamento);
    }

    @CacheEvict(value = "departamentos", allEntries = true)
    public Departamento actualizar(Long id, DepartamentoRequest request) {
        Departamento departamento = buscarPorId(id);
        departamento.setNombre(request.nombre());
        departamento.setDetalle(request.detalle());
        return departamentoRepository.save(departamento);
    }

    @CacheEvict(value = "departamentos", allEntries = true)
    public void eliminar(Long depId) {
        if (!departamentoRepository.existsById(depId)) {
            throw new EntityNotFoundException("Departamento" + depId);
        }
        departamentoRepository.deleteById(depId);
    }
}
