package com.distribuidora.sanmartin.services;

import com.distribuidora.sanmartin.models.Producto;
import com.distribuidora.sanmartin.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public void guardar(Producto producto) {
        productoRepository.save(producto);
    }
    
    // NUEVO: Métodos para eliminar y editar
    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }

    public Producto obtenerPorId(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }
}