package com.distribuidora.sanmartin.services;

import com.distribuidora.sanmartin.models.Envio;
import com.distribuidora.sanmartin.repository.EnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    // Cuanto tiempo debe pasar (desde que se despacho) para avanzar de estado.
    // Se pueden ajustar estos minutos segun que tan rapido quieras ver la demo.
    private static final long MINUTOS_HASTA_EN_CAMINO = 2;
    private static final long MINUTOS_HASTA_ENTREGADO = 4;

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

    /**
     * Simula el avance automatico de los despachos, sin intervencion manual.
     * Corre cada 1 minuto (60000 ms) y va avanzando cada envio segun cuanto
     * tiempo lleva desde que se registro (fechaDespacho):
     *
     *   En preparación --(2 min)--> En camino --(2 min mas)--> Entregado
     *
     * Los envios ya "Entregado" o "Cancelado" son estados finales y no se
     * tocan mas.
     */
    @Scheduled(fixedRate = 60000)
    public void simularAvanceDespachos() {
        List<Envio> envios = envioRepository.findAll();
        LocalDateTime ahora = LocalDateTime.now();

        for (Envio envio : envios) {
            String estado = envio.getEstadoEntrega();

            if ("En preparación".equals(estado)) {
                // Si por algun motivo no tiene fecha de despacho registrada,
                // la marcamos ahora como punto de partida de la simulacion.
                if (envio.getFechaDespacho() == null) {
                    envio.setFechaDespacho(ahora);
                    envioRepository.save(envio);
                    continue;
                }
                if (ahora.isAfter(envio.getFechaDespacho().plusMinutes(MINUTOS_HASTA_EN_CAMINO))) {
                    envio.setEstadoEntrega("En camino");
                    envioRepository.save(envio);
                }

            } else if ("En camino".equals(estado)) {
                if (envio.getFechaDespacho() != null
                        && ahora.isAfter(envio.getFechaDespacho().plusMinutes(MINUTOS_HASTA_ENTREGADO))) {
                    envio.setEstadoEntrega("Entregado");
                    envioRepository.save(envio);
                }
            }
            // "Entregado" y "Cancelado" son estados finales: no se modifican.
        }
    }
}