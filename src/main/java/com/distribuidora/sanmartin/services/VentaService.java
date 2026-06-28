package com.distribuidora.sanmartin.services;

import com.distribuidora.sanmartin.models.Cliente;
import com.distribuidora.sanmartin.models.DetalleVenta;
import com.distribuidora.sanmartin.models.ItemCarrito;
import com.distribuidora.sanmartin.models.Venta;
import com.distribuidora.sanmartin.repository.ClienteRepository;
import com.distribuidora.sanmartin.repository.DetalleVentaRepository;
import com.distribuidora.sanmartin.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public Venta crearVenta(List<ItemCarrito> carrito, Integer idUsuario) {
        // Buscar cliente asociado al usuario
        Cliente cliente = clienteRepository.findByIdUsuario(idUsuario);
        Integer idCliente = (cliente != null) ? cliente.getIdCliente() : null;

        // Calcular montos
        BigDecimal subtotal = carrito.stream()
            .map(i -> BigDecimal.valueOf(i.getSubtotal()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal igv = subtotal.multiply(BigDecimal.valueOf(0.18))
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(igv);

        // Crear venta
        Venta venta = new Venta();
        venta.setFechaVenta(LocalDateTime.now());
        venta.setSubtotal(subtotal);
        venta.setIgv(igv);
        venta.setDescuento(BigDecimal.ZERO);
        venta.setTotalPagar(total);
        venta.setEstadoPago("Pendiente");
        venta.setIdCliente(idCliente);
        Venta ventaGuardada = ventaRepository.save(venta);

        // Crear detalles
        for (ItemCarrito item : carrito) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdVenta(ventaGuardada.getIdVenta());
            detalle.setIdProducto(item.getIdProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioAplicado(BigDecimal.valueOf(item.getPrecioUnitario()));
            detalleVentaRepository.save(detalle);
        }

        return ventaGuardada;
    }

    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    public List<Venta> listarPorCliente(Integer idCliente) {
        return ventaRepository.findByIdCliente(idCliente);
    }
}