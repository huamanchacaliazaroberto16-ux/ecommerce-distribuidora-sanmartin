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

    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }

    public Producto obtenerPorId(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    public List<Producto> filtrarPorCategoria(Integer idCategoria) {
        return productoRepository.findByIdCategoria(idCategoria);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreProductoContainingIgnoreCase(nombre);
    }
}