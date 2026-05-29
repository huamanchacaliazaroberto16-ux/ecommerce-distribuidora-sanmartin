package com.distribuidora.sanmartin.controllers;

import com.distribuidora.sanmartin.models.Producto;
import com.distribuidora.sanmartin.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ViewController {

    @Autowired
    private ProductoService productoService;

    // ==========================================
    // 1. MUNDO PÚBLICO (Puerta de entrada)
    // ==========================================
    
    @GetMapping("/")
    public String inicio() {
        return "inicio"; // Carga el nuevo archivo inicio.html que crearemos
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Carga tu pantalla de inicio de sesión
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro"; // Carga tu pantalla de registro
    }

    @GetMapping("/tienda")
    public String mostrarCatalogo(Model model) {
        model.addAttribute("listaProductos", productoService.listarProductos());
        return "catalogo"; // Tu diseño profesional de tarjetas
    }

    // ==========================================
    // 2. MUNDO ADMINISTRATIVO (Inventario)
    // ==========================================

    @GetMapping("/admin")
    public String admin() {
        return "index"; // Tu panel de control original (Dashboard)
    }

    @GetMapping("/productos")
    public String listar(Model model) {
        model.addAttribute("listaProductos", productoService.listarProductos());
        return "productos";
    }

    @GetMapping("/productos/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("producto", new Producto());
        return "formulario-producto";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(Producto producto) {
        productoService.guardar(producto);
        return "redirect:/productos";
    }

    @GetMapping("/productos/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", productoService.obtenerPorId(id));
        return "formulario-producto";
    }

    @GetMapping("/productos/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }
}