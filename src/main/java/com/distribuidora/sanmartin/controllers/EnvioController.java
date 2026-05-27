package com.distribuidora.sanmartin.controllers;

import com.distribuidora.sanmartin.models.Envio;
import com.distribuidora.sanmartin.services.EnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envios") // URL base para el Front-end
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    // GET: Obtener todos los envíos
    @GetMapping
    public List<Envio> obtenerTodos() {
        return envioService.listarEnvios();
    }

    // GET: Obtener un envío por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerPorId(@PathVariable Integer id) {
        return envioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: Crear o registrar un nuevo envío
    @PostMapping
    public Envio registrarEnvio(@RequestBody Envio envio) {
        return envioService.guardarOActualizar(envio);
    }

    // PUT: Actualizar solo el estado de entrega (ideal para el repartidor)
    @PutMapping("/{id}/estado")
    public ResponseEntity<Envio> cambiarEstado(@PathVariable Integer id, @RequestParam String estado) {
        Envio envioActualizado = envioService.actualizarEstado(id, estado);
        if (envioActualizado != null) {
            return ResponseEntity.ok(envioActualizado);
        }
        return ResponseEntity.notFound().build();
    }
}