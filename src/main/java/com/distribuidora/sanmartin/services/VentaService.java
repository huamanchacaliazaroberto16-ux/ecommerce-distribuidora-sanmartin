package com.distribuidora.sanmartin.services;

import com.distribuidora.sanmartin.models.Cliente;
import com.distribuidora.sanmartin.models.DetalleVenta;
import com.distribuidora.sanmartin.models.ItemCarrito;
import com.distribuidora.sanmartin.models.Producto;
import com.distribuidora.sanmartin.models.Venta;
import com.distribuidora.sanmartin.repository.ClienteRepository;
import com.distribuidora.sanmartin.repository.DetalleVentaRepository;
import com.distribuidora.sanmartin.repository.ProductoRepository;
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

    @Autowired
    private ProductoRepository productoRepository;

    public Venta crearVenta(List<ItemCarrito> carrito, Integer idUsuario,
                            String tipoEntrega, String direccionEntrega,
                            String metodoPago) {
        Cliente cliente = clienteRepository.findByIdUsuario(idUsuario);
        Integer idCliente = (cliente != null) ? cliente.getIdCliente() : null;

        BigDecimal subtotal = carrito.stream()
            .map(i -> BigDecimal.valueOf(i.getSubtotal()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal igv = subtotal.multiply(BigDecimal.valueOf(0.18))
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(igv);

        Venta venta = new Venta();
        venta.setFechaVenta(LocalDateTime.now());
        venta.setSubtotal(subtotal);
        venta.setIgv(igv);
        venta.setDescuento(BigDecimal.ZERO);
        venta.setTotalPagar(total);
        venta.setEstadoPago("Pendiente");
        venta.setIdCliente(idCliente);
        venta.setTipoEntrega(tipoEntrega);
        venta.setDireccionEntrega("Domicilio".equals(tipoEntrega) ? direccionEntrega : null);
        venta.setMetodoPago(metodoPago != null ? metodoPago : "Efectivo");
        Venta ventaGuardada = ventaRepository.save(venta);

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

    public Venta actualizarEstadoPago(Integer idVenta, String nuevoEstado) {
        return ventaRepository.findById(idVenta).map(venta -> {
            String estadoAnterior = venta.getEstadoPago();
            venta.setEstadoPago(nuevoEstado);
            Venta ventaActualizada = ventaRepository.save(venta);

            // Solo tocamos el stock cuando el estado de pago realmente cambia
            // de/hacia "Pagado", para no descontarlo mas de una vez si el
            // admin guarda el mismo estado dos veces.
            boolean pasaAPagado = !"Pagado".equals(estadoAnterior) && "Pagado".equals(nuevoEstado);
            boolean dejaDeEstarPagado = "Pagado".equals(estadoAnterior) && !"Pagado".equals(nuevoEstado);

            if (pasaAPagado) {
                ajustarStockDeVenta(idVenta, -1);
            } else if (dejaDeEstarPagado) {
                ajustarStockDeVenta(idVenta, 1);
            }

            return ventaActualizada;
        }).orElse(null);
    }

    /**
     * Recorre los productos de una venta y ajusta el stock de cada uno.
     * signo = -1 para descontar (venta confirmada como Pagado)
     * signo = +1 para devolver (si un pedido pagado se cancela despues)
     */
    private void ajustarStockDeVenta(Integer idVenta, int signo) {
        List<DetalleVenta> detalles = detalleVentaRepository.findByIdVenta(idVenta);
        for (DetalleVenta detalle : detalles) {
            productoRepository.findById(detalle.getIdProducto()).ifPresent(producto -> {
                int stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;
                int nuevoStock = stockActual + (signo * detalle.getCantidad());
                producto.setStockActual(Math.max(nuevoStock, 0));
                productoRepository.save(producto);
            });
        }
    }

    public List<Venta> listarPorTipoEntrega(String tipoEntrega) {
        return ventaRepository.findByTipoEntrega(tipoEntrega);
    }
}