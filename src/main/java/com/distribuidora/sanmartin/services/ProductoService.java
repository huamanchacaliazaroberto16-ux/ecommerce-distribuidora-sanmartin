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

    public Producto registrarOActualizar(Producto producto) {
        return productoRepository.save(producto);
    }

    // Lógica del requerimiento de control de inventario
    public void evaluarAlertaInventario(Integer id) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null && producto.requiereAlerta()) {
            System.out.println("ALERT SUNAT/STOCK: El producto '" + producto.getNombreProducto() + 
                               "' alcanzó el stock mínimo de " + producto.getStockMinimo() + 
                               ". Stock actual: " + producto.getStockActual());
        }
    }
}