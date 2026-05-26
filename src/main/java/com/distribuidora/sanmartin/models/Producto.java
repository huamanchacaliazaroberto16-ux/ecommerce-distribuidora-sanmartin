package com.distribuidora.sanmartin.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "productos")
@Data // Esto genera automáticamente los Getters y Setters gracias a Lombok
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double precio;
    private Integer stockActual;
    private Integer stockMinimo; // Para controlar la alerta de stock mínimo

    // Método lógico de negocio para verificar si requiere alerta
    public boolean requiereAlertaStock() {
        return this.stockActual <= this.stockMinimo;
    }
}