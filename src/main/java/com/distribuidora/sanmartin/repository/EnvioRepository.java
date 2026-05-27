package com.distribuidora.sanmartin.repository;

import com.distribuidora.sanmartin.models.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Integer> {
}