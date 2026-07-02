package com.distribuidora.sanmartin.controllers;

import com.distribuidora.sanmartin.models.Categoria;
import com.distribuidora.sanmartin.models.Cliente;
import com.distribuidora.sanmartin.models.Envio;
import com.distribuidora.sanmartin.models.Producto;
import com.distribuidora.sanmartin.models.Usuario;
import com.distribuidora.sanmartin.repository.CategoriaRepository;
import com.distribuidora.sanmartin.repository.ClienteRepository;
import com.distribuidora.sanmartin.repository.RepartidorRepository;
import com.distribuidora.sanmartin.repository.UsuarioRepository;
import com.distribuidora.sanmartin.services.CategoriaService;
import com.distribuidora.sanmartin.services.EnvioService;
import com.distribuidora.sanmartin.services.ProductoService;
import com.distribuidora.sanmartin.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private CategoriaService categoriaService;

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
    public String mostrarCatalogo(
            @RequestParam(required = false) Integer categoria,
            @RequestParam(required = false) String buscar,
            Model model) {

        if (buscar != null && !buscar.isBlank()) {
            model.addAttribute("listaProductos", productoService.buscarPorNombre(buscar));
        } else if (categoria != null) {
            model.addAttribute("listaProductos", productoService.filtrarPorCategoria(categoria));
        } else {
            model.addAttribute("listaProductos", productoService.listarProductos());
        }

        model.addAttribute("listaCategorias", categoriaRepository.findAll());
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("buscarTexto", buscar);
        return "catalogo";
    }
    @GetMapping("/mis-pedidos")
public String misPedidos(Model model, Authentication authentication) {
    String username = authentication.getName();
    Usuario usuario = usuarioRepository.findByUsername(username);
    Cliente cliente = clienteRepository.findByIdUsuario(usuario.getId_usuario());
    if (cliente != null) {
        model.addAttribute("listaPedidos", ventaService.listarPorCliente(cliente.getIdCliente()));
    } else {
        model.addAttribute("listaPedidos", new java.util.ArrayList<>());
    }
    return "mis-pedidos";
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
        model.addAttribute("listaCategorias", categoriaRepository.findAll());
        return "formulario-producto";
    }

    @PostMapping("/productos/guardar")
     public String guardarProducto(@jakarta.validation.Valid Producto producto,
                               org.springframework.validation.BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
        model.addAttribute("listaCategorias", categoriaRepository.findAll());
        return "formulario-producto";
       }
        productoService.guardar(producto);
       return "redirect:/admin";
      }

    @GetMapping("/productos/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", productoService.obtenerPorId(id));
        model.addAttribute("listaCategorias", categoriaRepository.findAll());
        return "formulario-producto";
    }

    @GetMapping("/productos/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }

    @PostMapping("/categorias/crear-rapido")
    @ResponseBody
    public Categoria crearCategoriaRapido(@RequestParam String nombre) {
        Categoria nueva = new Categoria();
        nueva.setNombre(nombre);
        categoriaService.guardar(nueva);
        return nueva;
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

    @GetMapping("/clientes/{id}/pedidos")
    public String pedidosCliente(@PathVariable Integer id, Model model) {
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        model.addAttribute("cliente", cliente);
        model.addAttribute("listaPedidos", ventaService.listarPorCliente(id));
        return "pedidos-cliente";
    }
}