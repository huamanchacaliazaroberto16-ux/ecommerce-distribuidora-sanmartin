package com.distribuidora.sanmartin.controllers;

import com.distribuidora.sanmartin.models.Cliente;
import com.distribuidora.sanmartin.models.Envio;
import com.distribuidora.sanmartin.models.Producto;
import com.distribuidora.sanmartin.models.Usuario;
import com.distribuidora.sanmartin.repository.ClienteRepository;
import com.distribuidora.sanmartin.repository.RepartidorRepository;
import com.distribuidora.sanmartin.repository.UsuarioRepository;
import com.distribuidora.sanmartin.services.EnvioService;
import com.distribuidora.sanmartin.services.ProductoService;
import com.distribuidora.sanmartin.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ViewController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnvioService envioService;

    @Autowired
    private RepartidorRepository repartidorRepository;

    @Autowired
    private VentaService ventaService;

    // ==========================================
    // 1. MUNDO PÚBLICO
    // ==========================================

    @GetMapping("/")
    public String inicio() {
        return "inicio";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String username, @RequestParam String password) {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setPassword_hash(password);
        nuevoUsuario.setId_rol(2);
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombreCompleto(username);
        nuevoCliente.setIdUsuario(usuarioGuardado.getId_usuario());
        clienteRepository.save(nuevoCliente);

        return "redirect:/login";
    }

    @GetMapping("/tienda")
    public String mostrarCatalogo(Model model) {
        model.addAttribute("listaProductos", productoService.listarProductos());
        return "catalogo";
    }

    // ==========================================
    // 2. MUNDO ADMINISTRATIVO
    // ==========================================

    @GetMapping("/admin")
    public String admin() {
        return "index";
    }

    @GetMapping("/envios")
    public String envios(Model model) {
        // Solo mostrar pedidos con tipo Domicilio que aún no tienen envío
        model.addAttribute("listaEnvios", envioService.listarEnvios());
        model.addAttribute("listaRepartidores", repartidorRepository.findAll());
        model.addAttribute("listaPedidosDomicilio",
            ventaService.listarPorTipoEntrega("Domicilio"));
        return "envios";
    }

    @PostMapping("/envios/guardar")
    public String guardarEnvio(Envio envio) {
        envioService.guardarOActualizar(envio);
        return "redirect:/envios";
    }

    @PostMapping("/envios/estado")
    public String cambiarEstado(@RequestParam Integer id, @RequestParam String estado) {
        envioService.actualizarEstado(id, estado);
        return "redirect:/envios";
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
        return "redirect:/admin";
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

    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("listaPedidos", ventaService.listarTodas());
        return "pedidos";
    }

    @PostMapping("/pedidos/estado")
    public String cambiarEstadoPedido(@RequestParam Integer id,
                                      @RequestParam String estado) {
        ventaService.actualizarEstadoPago(id, estado);
        return "redirect:/pedidos";
    }

    @GetMapping("/clientes")
    public String clientes(Model model) {
        model.addAttribute("listaClientes", clienteRepository.findAll());
        return "clientes";
    }
}