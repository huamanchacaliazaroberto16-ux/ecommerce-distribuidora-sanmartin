package com.distribuidora.sanmartin.repository;

import com.distribuidora.sanmartin.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Aquí Spring ya creó por dentro los métodos buscarPorId, guardar, eliminar, etc.
}