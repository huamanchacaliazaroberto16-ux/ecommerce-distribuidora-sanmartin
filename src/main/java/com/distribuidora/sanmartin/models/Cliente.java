package com.distribuidora.sanmartin.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cliente")
@Data
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

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

    @Column(name = "dni", length = 8)
    private String dni;

    @Column(name = "celular", length = 9)
    private String celular;
}