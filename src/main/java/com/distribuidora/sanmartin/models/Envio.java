package com.distribuidora.sanmartin.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "envio")
@Data
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envio")
    private Integer idEnvio; // <-- El único cambio: de Long a Integer

    @Column(name = "id_venta")
    private Integer idVenta;

    @Column(name = "id_repartidor")
    private Integer idRepartidor;

    @Column(name = "fecha_despacho")
    private LocalDateTime fechaDespacho;

    @Column(name = "estado_entrega", length = 30)
    private String estadoEntrega = "En preparación";

    @Column(name = "direccion_entrega", columnDefinition = "TEXT")
    private String direccionEntrega;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}