package com.distribuidora.sanmartin.models;

import java.io.Serializable;
import lombok.Data;

@Data
public class DetalleVentaId implements Serializable {
    private Long idVenta;
    private Long idProducto;
}