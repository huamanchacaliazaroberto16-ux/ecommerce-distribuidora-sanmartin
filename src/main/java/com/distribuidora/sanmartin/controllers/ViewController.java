package com.distribuidora.sanmartin.controllers;

import com.distribuidora.sanmartin.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ViewController {

    @Autowired
    private ProductoService productoService;

    // Ruta de prueba rápida
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "El controlador está funcionando perfectamente!";
    }

    // Ruta raíz
    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    // Ruta productos con servicio activo
    @GetMapping("/productos")
    public String verPaginaProductos(Model model) {
        model.addAttribute("listaProductos", productoService.listarProductos());
        return "productos";
    }

    // Ruta envíos
    @GetMapping("/envios")
    public String envios() {
        return "envios";
    }
}