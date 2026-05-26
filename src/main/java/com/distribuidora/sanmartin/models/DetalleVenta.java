package com.distribuidora.sanmartin.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_venta")
@IdClass(DetalleVentaId.class) // Vincula la llave compuesta
@Data
public class DetalleVenta {

    @Id
    @Column(name = "id_venta")
    private Long idVenta;

    @Id
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_applied", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioAplicado;
}