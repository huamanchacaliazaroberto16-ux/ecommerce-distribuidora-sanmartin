package com.distribuidora.sanmartin.controllers;

import com.distribuidora.sanmartin.models.Producto;
import com.distribuidora.sanmartin.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos") // La URL base para el Front-end
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Ruta para obtener todos los productos en la web
    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoService.listarProductos();
    }

    // Ruta para registrar un nuevo producto desde un formulario
    @PostMapping
    public Producto guardarProducto(@RequestBody Producto producto) {
        return productoService.registrarOActualizar(producto);
    }
}