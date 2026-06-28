package com.distribuidora.sanmartin.repository;

import com.distribuidora.sanmartin.models.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepartidorRepository extends JpaRepository<Repartidor, Integer> {
}