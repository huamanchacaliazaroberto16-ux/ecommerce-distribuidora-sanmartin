package com.distribuidora.sanmartin.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "repartidor")
@Data
public class Repartidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_repartidor")
    private Integer idRepartidor;

    @Column(name = "nombre_completo")
    private String nombreCompleto;

    private String vehiculo;

    @Column(name = "placa_vehiculo")
    private String placaVehiculo;
}