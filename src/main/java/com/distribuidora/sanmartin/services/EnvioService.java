package com.distribuidora.sanmartin.services;

import com.distribuidora.sanmartin.models.Envio;
import com.distribuidora.sanmartin.repository.EnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    // Listar todos los envíos realizados o pendientes
    public List<Envio> listarEnvios() {
        return envioRepository.findAll();
    }

    // Buscar un envío específico por su ID
    public Optional<Envio> obtenerPorId(Integer id) {
        return envioRepository.findById(id);
    }

    // Registrar o actualizar un envío (asigna repartidor, dirección, etc.)
    public Envio guardarOActualizar(Envio envio) {
        return envioRepository.save(envio);
    }

    // Cambiar específicamente el estado del envío (Ej: "En camino", "Entregado")
    public Envio actualizarEstado(Integer id, String nuevoEstado) {
        return envioRepository.findById(id).map(envio -> {
            envio.setEstadoEntrega(nuevoEstado);
            return envioRepository.save(envio);
        }).orElse(null);
    }
}