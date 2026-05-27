package com.distribuidora.sanmartin.controllers;

import com.distribuidora.sanmartin.models.DetalleVenta;
import com.distribuidora.sanmartin.models.DetalleVentaId;
import com.distribuidora.sanmartin.repository.DetalleVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-venta")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @GetMapping
    public List<DetalleVenta> listarTodos() {
        return detalleVentaRepository.findAll();
    }

    // Buscar usando los dos identificadores de la clave compuesta
    @GetMapping("/{idVenta}/{idProducto}")
    public ResponseEntity<DetalleVenta> obtenerPorId(@PathVariable Integer idVenta, @PathVariable Integer idProducto) {
        DetalleVentaId id = new DetalleVentaId(idVenta, idProducto);
        return detalleVentaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DetalleVenta crearDetalle(@RequestBody DetalleVenta detalleVenta) {
        return detalleVentaRepository.save(detalleVenta);
    }

    // Actualizar usando la clave compuesta
    @PutMapping("/{idVenta}/{idProducto}")
    public ResponseEntity<DetalleVenta> actualizarDetalle(@PathVariable Integer idVenta, @PathVariable Integer idProducto, @RequestBody DetalleVenta detalles) {
        DetalleVentaId id = new DetalleVentaId(idVenta, idProducto);
        if (!detalleVentaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        detalles.setIdVenta(idVenta);
        detalles.setIdProducto(idProducto);
        DetalleVenta actualizado = detalleVentaRepository.save(detalles);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar usando la clave compuesta
    @DeleteMapping("/{idVenta}/{idProducto}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Integer idVenta, @PathVariable Integer idProducto) {
        DetalleVentaId id = new DetalleVentaId(idVenta, idProducto);
        if (!detalleVentaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        detalleVentaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}