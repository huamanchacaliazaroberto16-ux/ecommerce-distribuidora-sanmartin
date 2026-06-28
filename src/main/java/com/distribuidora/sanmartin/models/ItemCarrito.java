package com.distribuidora.sanmartin.models;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ItemCarrito {
    private Integer idProducto;
    private String nombreProducto;
    private Double precioUnitario;
    private Integer cantidad;

    public Double getSubtotal() {
        return precioUnitario * cantidad;
    }
}