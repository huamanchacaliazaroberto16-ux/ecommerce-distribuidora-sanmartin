package com.distribuidora.sanmartin.repository;

import com.distribuidora.sanmartin.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByIdCategoria(Integer idCategoria);
    List<Producto> findByNombreProductoContainingIgnoreCase(String nombre);
}