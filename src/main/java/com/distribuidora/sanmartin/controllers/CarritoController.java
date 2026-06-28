package com.distribuidora.sanmartin.controllers;

import com.distribuidora.sanmartin.models.ItemCarrito;
import com.distribuidora.sanmartin.models.Producto;
import com.distribuidora.sanmartin.services.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Ver carrito
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        double total = carrito.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);
        return "carrito";
    }

    // Agregar producto
    @GetMapping("/agregar/{id}")
    public String agregar(@PathVariable Integer id, HttpSession session) {
        Producto producto = productoService.obtenerPorId(id);
        List<ItemCarrito> carrito = obtenerCarrito(session);

        // Si ya existe el producto, incrementa cantidad
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

    // Eliminar producto
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, HttpSession session) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        carrito.removeIf(item -> item.getIdProducto().equals(id));
        session.setAttribute("carrito", carrito);
        return "redirect:/carrito";
    }

    // Vaciar carrito
    @GetMapping("/vaciar")
    public String vaciar(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/carrito";
    }

    // Confirmar pedido
    @PostMapping("/confirmar")
    public String confirmar(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/tienda?pedido=ok";
    }

    // Método auxiliar
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