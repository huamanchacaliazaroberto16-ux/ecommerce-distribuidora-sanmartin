package com.distribuidora.sanmartin.controllers;

import com.distribuidora.sanmartin.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Usamos @Controller tradicional para renderizar HTML con Thymeleaf
public class ViewController {

    @Autowired
    private ProductoService productoService;

    // Cuando el usuario entre a http://localhost:8080/productos, se cargará el HTML
    @GetMapping("/productos")
    public String verPaginaProductos(Model model) {
        // Jalamos los productos reales de tu base de datos de pgAdmin
        model.addAttribute("listaProductos", productoService.listarProductos());
        
        // Retorna el nombre exacto del archivo HTML sin la extensión
        return "productos"; 
    }
}