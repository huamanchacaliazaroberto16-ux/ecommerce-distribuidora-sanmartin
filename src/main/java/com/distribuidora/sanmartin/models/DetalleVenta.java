package com.distribuidora.sanmartin.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "detalle_venta")
@IdClass(DetalleVentaId.class)
@Data
public class DetalleVenta {

    @Id
    @Column(name = "id_venta")
    private Integer idVenta;

    @Id
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    // Cambiado de precio_unitario a precio_aplicado para que coincida con tu pgAdmin
    @Column(name = "precio_aplicado", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal precioAplicado; 
}