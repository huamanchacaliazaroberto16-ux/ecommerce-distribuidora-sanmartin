package com.distribuidora.sanmartin.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_usuario;

    private String username;
    private String password_hash;
    private Integer id_rol;

    @Column(name = "dni", length = 8)
    private String dni;

    @Column(name = "celular", length = 9)
    private String celular;
}