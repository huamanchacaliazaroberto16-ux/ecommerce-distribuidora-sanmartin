package com.distribuidora.sanmartin.controllers;

import com.distribuidora.sanmartin.models.Producto;
import com.distribuidora.sanmartin.services.ProductoService;
import com.distribuidora.sanmartin.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList; // Import agregado

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @PostMapping("/consultar")
    public Map<String, String> consultar(@RequestBody Map<String, String> body) {
        String pregunta = body.get("pregunta").toLowerCase().trim();
        String respuesta = procesarPregunta(pregunta);
        return Map.of("respuesta", respuesta);
    }

    private String procesarPregunta(String pregunta) {
        List<Producto> todosLosProductos = productoService.listarProductos();

        // Saludos
        if (contiene(pregunta, "hola", "buenas", "buenos", "hey", "hi")) {
            return "¡Hola! 👋 Soy el asistente virtual de Distribuidora San Martín. Puedo ayudarte con:\n" +
                   "• Consultar productos y precios\n" +
                   "• Ver disponibilidad de stock\n" +
                   "• Información sobre categorías\n" +
                   "• Cómo realizar un pedido\n\n" +
                   "¿En qué te puedo ayudar?";
        }

        // Despedidas
        if (contiene(pregunta, "adios", "chao", "bye", "hasta luego", "gracias")) {
            return "¡Hasta luego! 😊 Fue un placer ayudarte. Si necesitas algo más, no dudes en escribirnos.";
        }

        // Preguntar por todos los productos
        if (contiene(pregunta, "todos", "lista", "catálogo", "catalogo", "productos disponibles", "qué tienen", "que tienen")) {
            if (todosLosProductos.isEmpty()) {
                return "Por el momento no tenemos productos registrados. Contáctanos al 342-444-0263.";
            }
            StringBuilder sb = new StringBuilder("📦 Nuestros productos disponibles:\n\n");
            for (Producto p : todosLosProductos) {
                sb.append("• ").append(p.getNombreProducto())
                  .append(" — S/. ").append(p.getPrecioUnitario())
                  .append(" (Stock: ").append(p.getStockActual()).append(")\n");
            }
            return sb.toString();
        }

        // Consulta de stock bajo o agotado
        if (contiene(pregunta, "agotado", "sin stock", "disponible", "hay stock", "queda")) {
            List<Producto> sinStock = todosLosProductos.stream()
                .filter(p -> p.getStockActual() == 0)
                .collect(Collectors.toList());
            List<Producto> conStock = todosLosProductos.stream()
                .filter(p -> p.getStockActual() > 0)
                .collect(Collectors.toList());

            if (sinStock.isEmpty()) {
                return "✅ ¡Buenas noticias! Todos nuestros productos tienen stock disponible en este momento.";
            }
            StringBuilder sb = new StringBuilder("📊 Estado del stock:\n\n");
            sb.append("✅ Con stock (").append(conStock.size()).append(" productos)\n");
            sb.append("❌ Sin stock:\n");
            for (Producto p : sinStock) {
                sb.append("  • ").append(p.getNombreProducto()).append("\n");
            }
            return sb.toString();
        }

        // Categorías
        if (contiene(pregunta, "categoría", "categoria", "categorias", "sección", "seccion", "tipos")) {
            var categorias = categoriaRepository.findAll();
            if (categorias.isEmpty()) {
                return "Por el momento no tenemos categorías registradas.";
            }
            StringBuilder sb = new StringBuilder("🏷️ Nuestras categorías:\n\n");
            for (var cat : categorias) {
                long count = todosLosProductos.stream()
                    .filter(p -> cat.getIdCategoria().equals(p.getIdCategoria()))
                    .count();
                sb.append("• ").append(cat.getNombre())
                  .append(" (").append(count).append(" productos)\n");
            }
            return sb.toString();
        }

        // Precio más barato
        if (contiene(pregunta, "barato", "económico", "economico", "más barato", "mas barato", "menor precio")) {
            return todosLosProductos.stream()
                .min((a, b) -> a.getPrecioUnitario().compareTo(b.getPrecioUnitario()))
                .map(p -> "💰 El producto más económico es:\n\n" +
                          "• " + p.getNombreProducto() + "\n" +
                          "• Precio: S/. " + p.getPrecioUnitario() + "\n" +
                          "• Stock: " + p.getStockActual() + " unidades")
                .orElse("No encontré productos en este momento.");
        }

        // Precio más caro
        if (contiene(pregunta, "caro", "premium", "más caro", "mas caro", "mayor precio")) {
            return todosLosProductos.stream()
                .max((a, b) -> a.getPrecioUnitario().compareTo(b.getPrecioUnitario()))
                .map(p -> "💎 El producto premium es:\n\n" +
                          "• " + p.getNombreProducto() + "\n" +
                          "• Precio: S/. " + p.getPrecioUnitario() + "\n" +
                          "• Stock: " + p.getStockActual() + " unidades")
                .orElse("No encontré productos en este momento.");
        }

        // Cómo hacer un pedido
        if (contiene(pregunta, "pedido", "comprar", "como compro", "cómo compro", "proceso", "como pido")) {
            return "🛒 Para realizar un pedido:\n\n" +
                   "1. Regístrate o inicia sesión\n" +
                   "2. Explora el catálogo y agrega productos al carrito\n" +
                   "3. Ve al carrito y ajusta las cantidades\n" +
                   "4. Elige tu método de pago (Efectivo, Yape o Tarjeta)\n" +
                   "5. Selecciona recojo en tienda o envío a domicilio\n" +
                   "6. Confirma tu pedido y descarga tu comprobante\n\n" +
                   "¿Necesitas ayuda con algún paso?";
        }

        // Métodos de pago
        if (contiene(pregunta, "pago", "pagar", "yape", "tarjeta", "efectivo", "transferencia")) {
            return "💳 Métodos de pago disponibles:\n\n" +
                   "• 💵 Efectivo — Al momento de recoger o recibir\n" +
                   "• 📱 Yape — Al número 987-654-321\n" +
                   "• 💳 Tarjeta — Débito o crédito\n\n" +
                   "Todos los pagos son seguros y recibirás tu comprobante digital.";
        }

        // Envío y entrega
        if (contiene(pregunta, "envío", "envio", "delivery", "domicilio", "entrega", "despacho", "llegan")) {
            return "🚚 Opciones de entrega:\n\n" +
                   "• 🏪 Recojo en tienda — Sin costo adicional\n" +
                   "  Dirección: Pachacutec, Carretera de Tate, Ica\n\n" +
                   "• 🚚 Envío a domicilio — Cobertura en Ica\n" +
                   "  Ingresa tu dirección al confirmar el pedido\n\n" +
                   "📞 Para más info: 342-444-0263";
        }

        // Contacto
        if (contiene(pregunta, "contacto", "teléfono", "telefono", "llamar", "whatsapp", "dirección", "direccion", "ubicación", "ubicacion")) {
            return "📞 Información de contacto:\n\n" +
                   "• Teléfono: 342-444-0263\n" +
                   "• Email: pedidos@distribuidorasanmartin.com\n" +
                   "• Dirección: Pachacutec, Carretera de Tate, Ica\n" +
                   "• Horario: Lunes a Sábado 8am - 6pm";
        }

        // Devoluciones
        if (contiene(pregunta, "devolución", "devolucion", "cambio", "garantía", "garantia", "reclamo")) {
            return "🔄 Política de devoluciones:\n\n" +
                   "• Tienes 7 días para solicitar cambio o devolución\n" +
                   "• El producto debe estar en su estado original\n" +
                   "• Contacta al 342-444-0263 con tu número de pedido\n\n" +
                   "Estamos para ayudarte 😊";
        }

        // Búsqueda de producto específico por nombre — más flexible
        List<String> palabrasClave = extraerPalabrasClave(pregunta);
        List<Producto> encontrados = new ArrayList<>();
        for (Producto p : todosLosProductos) {
            String nombreLower = p.getNombreProducto().toLowerCase();
            String descLower = p.getDescripcion() != null ? p.getDescripcion().toLowerCase() : "";
            for (String clave : palabrasClave) {
                if (clave.length() > 2 && (nombreLower.contains(clave) || descLower.contains(clave))) {
                    if (!encontrados.contains(p)) {
                        encontrados.add(p);
                    }
                    break;
                }
            }
        }
        if (!encontrados.isEmpty()) {
            StringBuilder sb = new StringBuilder("🔍 Encontré esto para ti:\n\n");
            for (Producto p : encontrados) {
                sb.append("📦 ").append(p.getNombreProducto()).append("\n");
                sb.append("   💰 Precio: S/. ").append(p.getPrecioUnitario()).append("\n");
                sb.append("   📊 Stock: ").append(p.getStockActual()).append(" unidades");
                if (p.getStockActual() == 0) {
                    sb.append(" ❌ Agotado");
                } else if (p.getStockActual() <= p.getStockMinimo()) {
                    sb.append(" ⚠️ Stock bajo");
                } else {
                    sb.append(" ✅ Disponible");
                }
                if (p.getDescripcion() != null && !p.getDescripcion().isBlank()) {
                    sb.append("\n   📝 ").append(p.getDescripcion());
                }
                sb.append("\n\n");
            }
            return sb.toString();
        }

        // Respuesta por defecto
        return "🤔 No entendí tu consulta. Puedes preguntarme sobre:\n\n" +
               "• Productos y precios (ej: '¿tienen laptops?')\n" +
               "• Stock disponible\n" +
               "• Categorías\n" +
               "• Cómo hacer un pedido\n" +
               "• Métodos de pago\n" +
               "• Envíos y entregas\n" +
               "• Contacto\n\n" +
               "📞 También puedes llamarnos al 342-444-0263";
    }

    private boolean contiene(String texto, String... palabras) {
        for (String palabra : palabras) {
            if (texto.contains(palabra)) return true;
        }
        return false;
    }

    private List<String> extraerPalabrasClave(String pregunta) {
        // Quita acentos para comparar mejor
        String normalizada = pregunta
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ü", "u")
            .replace("¿", "").replace("?", "").replace("¡", "").replace("!", "");
        String[] stopWords = {
            "tienen", "hay", "tiene", "cuanto", "cuánto", "cuesta", "vale",
            "precio", "stock", "disponible", "queda", "quedan", "el", "la",
            "los", "las", "un", "una", "de", "del", "me", "puedo", "ver",
            "que", "qué", "como", "cómo", "donde", "dónde", "cuando",
            "cuándo", "por", "para", "con", "sin", "sobre", "entre",
            "algún", "algun", "alguna", "tienen", "busco", "quiero",
            "necesito", "marca", "marcas", "celular", "celulares",
            "producto", "productos", "modelo", "modelos", "tipo", "tipos",
            "muestren", "muestrame", "dime", "buscar", "encontrar"
        };
        List<String> palabras = new ArrayList<>();
        for (String palabra : normalizada.split("\\s+")) {
            palabra = palabra.trim().toLowerCase();
            boolean esStop = false;
            for (String stop : stopWords) {
                if (palabra.equals(stop)) { esStop = true; break; }
            }
            if (!esStop && palabra.length() > 2) {
                palabras.add(palabra);
            }
        }
        return palabras;
    }
}