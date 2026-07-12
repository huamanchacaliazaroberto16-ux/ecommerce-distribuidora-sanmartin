package com.distribuidora.sanmartin.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "venta")
@Data
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta = LocalDateTime.now();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal igv;

    @Column(precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "total_pagar", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPagar;

    @Column(name = "estado_pago", length = 20)
    private String estadoPago = "Pendiente";

    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "tipo_entrega", length = 20)
    private String tipoEntrega = "Tienda";

    @Column(name = "direccion_entrega", columnDefinition = "TEXT")
    private String direccionEntrega;

    @Column(name = "metodo_pago", length = 20)
    private String metodoPago = "Efectivo";
}