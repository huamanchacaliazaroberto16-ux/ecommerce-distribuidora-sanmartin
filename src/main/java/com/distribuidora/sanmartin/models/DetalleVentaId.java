package com.distribuidora.sanmartin.models;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaId implements Serializable {
    private Integer idVenta;
    private Integer idProducto;
}