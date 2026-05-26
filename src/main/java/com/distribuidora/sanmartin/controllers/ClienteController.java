package com.distribuidora.sanmartin.controllers;

import com.distribuidora.sanmartin.models.Cliente;
import com.distribuidora.sanmartin.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    // 1. LISTAR TODOS LOS CLIENTES
    @GetMapping
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    // 2. BUSCAR UN CLIENTE POR ID
    @GetMapping("/{id}")
    public Cliente obtenerPorId(@PathVariable Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    // 3. REGISTRAR O ACTUALIZAR UN CLIENTE (Soporta los datos de tu script)
    @PostMapping
    public Cliente guardar(@RequestBody Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // 4. ELIMINAR UN CLIENTE
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        clienteRepository.deleteById(id);
    }
}