package com.distribuidora.sanmartin.controllers;

import com.distribuidora.sanmartin.models.Cliente;
import com.distribuidora.sanmartin.models.ItemCarrito;
import com.distribuidora.sanmartin.models.Producto;
import com.distribuidora.sanmartin.models.Usuario;
import com.distribuidora.sanmartin.models.Venta;
import com.distribuidora.sanmartin.repository.ClienteRepository;
import com.distribuidora.sanmartin.repository.UsuarioRepository;
import com.distribuidora.sanmartin.services.ProductoService;
import com.distribuidora.sanmartin.services.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        double total = carrito.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);
        return "carrito";
    }

    @GetMapping("/agregar/{id}")
    public String agregar(@PathVariable Integer id, HttpSession session) {
        Producto producto = productoService.obtenerPorId(id);
        List<ItemCarrito> carrito = obtenerCarrito(session);

        boolean encontrado = false;
        for (ItemCarrito item : carrito) {
            if (item.getIdProducto().equals(id)) {
                item.setCantidad(item.getCantidad() + 1);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            carrito.add(new ItemCarrito(
                producto.getIdProducto(),
                producto.getNombreProducto(),
                producto.getPrecioUnitario().doubleValue(),
                1
            ));
        }
        session.setAttribute("carrito", carrito);
        return "redirect:/tienda";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, HttpSession session) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        carrito.removeIf(item -> item.getIdProducto().equals(id));
        session.setAttribute("carrito", carrito);
        return "redirect:/carrito";
    }

    @PostMapping("/cantidad")
    public String cambiarCantidad(@RequestParam Integer idProducto,
                                   @RequestParam Integer cantidad,
                                   HttpSession session) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        if (cantidad <= 0) {
            carrito.removeIf(item -> item.getIdProducto().equals(idProducto));
        } else {
            for (ItemCarrito item : carrito) {
                if (item.getIdProducto().equals(idProducto)) {
                    item.setCantidad(cantidad);
                    break;
                }
            }
        }
        session.setAttribute("carrito", carrito);
        return "redirect:/carrito";
    }

    @GetMapping("/vaciar")
    public String vaciar(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/carrito";
    }

    @PostMapping("/confirmar")
    public String confirmar(HttpSession session,
                            Authentication authentication,
                            @RequestParam String tipoEntrega,
                            @RequestParam(required = false) String direccionEntrega,
                            @RequestParam(required = false) String metodoPago) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        if (!carrito.isEmpty()) {
            String username = authentication.getName();
            Usuario usuario = usuarioRepository.findByUsername(username);

            List<ItemCarrito> resumen = new ArrayList<>(carrito);
            session.setAttribute("ultimoResumen", resumen);
            session.setAttribute("ultimoUsuario", usuario);

            // Buscar cliente para mostrar nombre completo en comprobante
            Cliente clienteActual = clienteRepository.findByIdUsuario(usuario.getId_usuario());
            session.setAttribute("ultimoCliente", clienteActual);

            Venta venta = ventaService.crearVenta(
                carrito,
                usuario.getId_usuario(),
                tipoEntrega,
                direccionEntrega,
                metodoPago
            );
            session.setAttribute("ultimaVenta", venta);
            session.removeAttribute("carrito");
        }
        return "redirect:/carrito/resumen";
    }

    @GetMapping("/resumen")
    public String resumen(HttpSession session, Model model) {
        Venta venta = (Venta) session.getAttribute("ultimaVenta");
        List<ItemCarrito> resumen = (List<ItemCarrito>) session.getAttribute("ultimoResumen");
        Cliente cliente = (Cliente) session.getAttribute("ultimoCliente");

        if (venta == null) {
            return "redirect:/tienda";
        }

        double total = resumen != null
            ? resumen.stream().mapToDouble(ItemCarrito::getSubtotal).sum()
            : 0;

        model.addAttribute("venta", venta);
        model.addAttribute("resumen", resumen);
        model.addAttribute("total", total);
        model.addAttribute("cliente", cliente);
        return "resumen-pedido";
    }

    @SuppressWarnings("unchecked")
    private List<ItemCarrito> obtenerCarrito(HttpSession session) {
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }
}