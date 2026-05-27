package com.distribuidora.sanmartin.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "producto")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "nombre_producto", nullable = false, length = 100)
    private String nombreProducto;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Column(name = "stock_minimo")
    private Integer stockMinimo = 5; // Tu DEFAULT 5 del SQL

    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(name = "id_proveedor")
    private Integer idProveedor;

    // Lógica para tu requerimiento de Alerta de Stock Mínimo
    public boolean requiereAlerta() {
        return this.stockActual <= this.stockMinimo;
    }
}