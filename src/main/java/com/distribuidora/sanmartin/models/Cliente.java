package com.distribuidora.sanmartin.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cliente")
@Data
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente") // Mapea exactamente tu clave primaria de pgAdmin
    private Integer idCliente; // Cambiado de Long a Integer para que coincida con tu base de datos

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(name = "tipo_documento", length = 20)
    private String tipoDocumento;

    @Column(name = "numero_documento", length = 20)
    private String numeroDocumento;

    @Column(name = "correo", length = 100)
    private String correo;

    @Column(name = "direccion_fiscal", columnDefinition = "TEXT")
    private String direccionFiscal;

    @Column(name = "id_usuario") 
    private Integer idUsuario;
}