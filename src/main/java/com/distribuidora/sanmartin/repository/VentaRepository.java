package com.distribuidora.sanmartin.repository;

import com.distribuidora.sanmartin.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
}