package com.distribuidora.sanmartin.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categoria")
@Data
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria") // Mapea tu clave primaria de pgAdmin
    private Integer idCategoria;

    // Cambiado a "nombre_categoria" para coincidir con el estándar de tus tablas. 
    // Si en tu pgAdmin se llama diferente, cambia solo lo que está entre comillas.
    @Column(name = "nombre_categoria", nullable = false, length = 100) 
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
}