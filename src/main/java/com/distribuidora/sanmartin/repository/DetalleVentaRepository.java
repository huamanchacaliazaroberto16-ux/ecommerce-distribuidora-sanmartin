package com.distribuidora.sanmartin.repository;

import com.distribuidora.sanmartin.models.DetalleVenta;
import com.distribuidora.sanmartin.models.DetalleVentaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, DetalleVentaId> {
    List<DetalleVenta> findByIdVenta(Integer idVenta);
}