package com.distribuidora.sanmartin.repository;

import com.distribuidora.sanmartin.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    List<Venta> findByIdCliente(Integer idCliente);
    List<Venta> findByTipoEntrega(String tipoEntrega);
}