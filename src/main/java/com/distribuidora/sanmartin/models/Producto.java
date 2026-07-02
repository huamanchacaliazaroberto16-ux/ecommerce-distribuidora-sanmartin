package com.distribuidora.sanmartin.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(name = "nombre_producto", nullable = false, length = 100)
    private String nombreProducto;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Column(name = "stock_minimo")
    private Integer stockMinimo = 5;

    @NotNull(message = "Debes seleccionar una categoría")
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(name = "id_proveedor")
    private Integer idProveedor;

    @Column(name = "url_imagen")
    private String urlImagen;

    public boolean requiereAlerta() {
        return this.stockActual != null && this.stockMinimo != null && this.stockActual <= this.stockMinimo;
    }
}