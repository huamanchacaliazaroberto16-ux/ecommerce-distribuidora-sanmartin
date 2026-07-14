package com.distribuidora.sanmartin.controllers;

import com.distribuidora.sanmartin.models.Producto;
import com.distribuidora.sanmartin.services.ProductoService;
import com.distribuidora.sanmartin.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @PostMapping("/consultar")
    public Map<String, String> consultar(@RequestBody Map<String, String> body) {
        String pregunta = normalizar(body.get("pregunta"));
        String respuesta = procesarPregunta(pregunta);
        return Map.of("respuesta", respuesta);
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        return texto.toLowerCase()
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ü", "u")
            .replace("à", "a").replace("è", "e").replace("ì", "i")
            .replace("ò", "o").replace("ù", "u")
            .replace("¿", "").replace("?", "").replace("¡", "").replace("!", "")
            .replace(",", "").replace(".", "").replace(";", "").replace(":", "")
            .trim();
    }

    private String procesarPregunta(String p) {
        List<Producto> todos = productoService.listarProductos();

        // =====================
        // SALUDOS
        // =====================
        if (contiene(p, "hola", "buenas", "buenos dias", "buenos tardes", "buenas noches",
                "hey", "hi", "ola", "saludos", "buen dia", "buenas tarde", "que tal",
                "como estas", "como esta", "holi", "holaa")) {
            return "¡Hola! 👋 Bienvenido al asistente virtual de Distribuidora San Martín.\n\n" +
                   "Puedo ayudarte con:\n" +
                   "• 📦 Consultar productos y precios\n" +
                   "• 📊 Ver disponibilidad de stock\n" +
                   "• 🏷️ Ver categorías disponibles\n" +
                   "• 🛒 Cómo realizar un pedido\n" +
                   "• 💳 Métodos de pago\n" +
                   "• 🚚 Información de envíos\n" +
                   "• 📞 Datos de contacto\n\n" +
                   "¿En qué puedo ayudarte hoy?";
        }

        // =====================
        // DESPEDIDAS Y AGRADECIMIENTOS
        // =====================
        if (contiene(p, "adios", "chao", "bye", "hasta luego", "hasta pronto", "nos vemos",
                "me voy", "gracias", "muchas gracias", "thank", "ok gracias", "listo gracias",
                "perfecto gracias", "genial gracias", "excelente gracias")) {
            return "¡Hasta luego! 😊 Fue un placer ayudarte.\n\n" +
                   "Recuerda que puedes contactarnos al 📞 342-444-0263.\n" +
                   "¡Vuelve pronto a Distribuidora San Martín!";
        }

        // =====================
        // TODOS LOS PRODUCTOS
        // =====================
        if (contiene(p, "todos los productos", "lista de productos", "catalogo", "catalogo completo",
                "que productos tienen", "que venden", "que tienen", "que productos", "ver todo",
                "mostrar todo", "mostrar productos", "listar productos", "productos disponibles",
                "que hay disponible", "que tienen disponible", "menu", "inventario")) {
            if (todos.isEmpty()) return "Por el momento no tenemos productos registrados. Contáctanos al 342-444-0263.";
            StringBuilder sb = new StringBuilder("📦 Nuestros productos disponibles:\n\n");
            for (Producto prod : todos) {
                sb.append("• ").append(prod.getNombreProducto())
                  .append(" — S/. ").append(prod.getPrecioUnitario())
                  .append(prod.getStockActual() == 0 ? " ❌" : " ✅").append("\n");
            }
            sb.append("\nEscribe el nombre de un producto para más detalles.");
            return sb.toString();
        }

        // =====================
        // STOCK / DISPONIBILIDAD
        // =====================
        if (contiene(p, "stock", "disponible", "disponibilidad", "agotado", "agotados",
                "sin stock", "hay stock", "queda", "quedan", "tienen en stock",
                "cuantos quedan", "cuanto queda", "existe", "existen", "tienen unidades")) {
            List<Producto> sinStock = todos.stream().filter(pr -> pr.getStockActual() == 0).collect(Collectors.toList());
            List<Producto> stockBajo = todos.stream().filter(pr -> pr.getStockActual() > 0 && pr.getStockActual() <= pr.getStockMinimo()).collect(Collectors.toList());
            List<Producto> conStock = todos.stream().filter(pr -> pr.getStockActual() > pr.getStockMinimo()).collect(Collectors.toList());

            StringBuilder sb = new StringBuilder("📊 Estado del inventario:\n\n");
            sb.append("✅ Con stock normal: ").append(conStock.size()).append(" productos\n");
            sb.append("⚠️ Stock bajo: ").append(stockBajo.size()).append(" productos\n");
            sb.append("❌ Agotados: ").append(sinStock.size()).append(" productos\n");
            if (!sinStock.isEmpty()) {
                sb.append("\nProductos agotados:\n");
                sinStock.forEach(pr -> sb.append("  • ").append(pr.getNombreProducto()).append("\n"));
            }
            if (!stockBajo.isEmpty()) {
                sb.append("\nStock bajo:\n");
                stockBajo.forEach(pr -> sb.append("  • ").append(pr.getNombreProducto())
                    .append(" (").append(pr.getStockActual()).append(" unid.)\n"));
            }
            return sb.toString();
        }

        // =====================
        // CATEGORÍAS
        // =====================
        if (contiene(p, "categoria", "categorias", "seccion", "secciones", "tipos de productos",
                "tipo de producto", "rubros", "rubro", "clasificacion", "departamentos",
                "que categorias", "cuantas categorias", "que tipos")) {
            var cats = categoriaRepository.findAll();
            if (cats.isEmpty()) return "Por el momento no tenemos categorías registradas.";
            StringBuilder sb = new StringBuilder("🏷️ Nuestras categorías:\n\n");
            for (var cat : cats) {
                long count = todos.stream().filter(pr -> cat.getIdCategoria().equals(pr.getIdCategoria())).count();
                sb.append("• ").append(cat.getNombre()).append(" (").append(count).append(" productos)\n");
            }
            return sb.toString();
        }

        // =====================
        // PRECIO MÁS BARATO
        // =====================
        if (contiene(p, "mas barato", "mas economico", "menor precio", "precio mas bajo",
                "barato", "economico", "oferta", "ofertas", "precio minimo", "lo mas barato",
                "producto economico", "productos economicos", "precio bajo")) {
            return todos.stream()
                .min(Comparator.comparing(Producto::getPrecioUnitario))
                .map(pr -> "💰 El producto más económico:\n\n" +
                           "📦 " + pr.getNombreProducto() + "\n" +
                           "💵 Precio: S/. " + pr.getPrecioUnitario() + "\n" +
                           "📊 Stock: " + pr.getStockActual() + " unidades")
                .orElse("No hay productos registrados.");
        }

        // =====================
        // PRECIO MÁS CARO
        // =====================
        if (contiene(p, "mas caro", "precio mayor", "precio mas alto", "premium", "caro",
                "lo mas caro", "producto premium", "mayor precio", "precio maximo")) {
            return todos.stream()
                .max(Comparator.comparing(Producto::getPrecioUnitario))
                .map(pr -> "💎 El producto premium:\n\n" +
                           "📦 " + pr.getNombreProducto() + "\n" +
                           "💵 Precio: S/. " + pr.getPrecioUnitario() + "\n" +
                           "📊 Stock: " + pr.getStockActual() + " unidades")
                .orElse("No hay productos registrados.");
        }

        // =====================
        // RANGO DE PRECIOS
        // =====================
        if (contiene(p, "rango de precio", "entre", "precio entre", "cuanto cuestan",
                "cuanto cuesta en promedio", "precio promedio", "precios")) {
            if (todos.isEmpty()) return "No hay productos registrados.";
            var min = todos.stream().min(Comparator.comparing(Producto::getPrecioUnitario)).get();
            var max = todos.stream().max(Comparator.comparing(Producto::getPrecioUnitario)).get();
            return "💰 Rango de precios:\n\n" +
                   "• Desde: S/. " + min.getPrecioUnitario() + " (" + min.getNombreProducto() + ")\n" +
                   "• Hasta: S/. " + max.getPrecioUnitario() + " (" + max.getNombreProducto() + ")\n\n" +
                   "¿Te interesa algún rango específico?";
        }

        // =====================
        // CÓMO HACER UN PEDIDO
        // =====================
        if (contiene(p, "pedido", "como compro", "como hago", "como realizo", "proceso de compra",
                "como pido", "pasos para comprar", "comprar", "como se compra", "como funciona",
                "proceso", "quiero comprar", "quiero pedir", "hacer un pedido", "realizar pedido",
                "como hacer un pedido", "registrar pedido", "pasos")) {
            return "🛒 Para realizar tu pedido:\n\n" +
                   "1️⃣ Regístrate o inicia sesión en la web\n" +
                   "2️⃣ Explora el catálogo de productos\n" +
                   "3️⃣ Agrega los productos al carrito 🛒\n" +
                   "4️⃣ Ajusta las cantidades si es necesario\n" +
                   "5️⃣ Elige tu método de pago\n" +
                   "6️⃣ Selecciona recojo en tienda o envío\n" +
                   "7️⃣ Confirma y descarga tu comprobante 📄\n\n" +
                   "¿Necesitas ayuda con algún paso específico?";
        }

        // =====================
        // MÉTODOS DE PAGO
        // =====================
        if (contiene(p, "pago", "pagar", "yape", "tarjeta", "efectivo", "transferencia",
                "como pago", "formas de pago", "metodo de pago", "metodos de pago",
                "aceptan tarjeta", "aceptan yape", "pago en efectivo", "visa", "mastercard",
                "debito", "credito", "billetera digital")) {
            return "💳 Métodos de pago disponibles:\n\n" +
                   "• 💵 Efectivo — Al recoger o recibir tu pedido\n" +
                   "• 📱 Yape — Número: 987-654-321\n" +
                   "• 💳 Tarjeta — Débito o crédito (Visa/Mastercard)\n\n" +
                   "Todos los pagos son seguros ✅\n" +
                   "Recibirás tu comprobante digital al finalizar.";
        }

        // =====================
        // ENVÍO Y ENTREGA
        // =====================
        if (contiene(p, "envio", "delivery", "domicilio", "entrega", "despacho", "llegan",
                "reparto", "tiempo de entrega", "cuando llega", "cuanto demora", "dias de entrega",
                "zona de cobertura", "cobertura", "donde entregan", "hacen delivery",
                "llevan a domicilio", "costo de envio", "precio de envio", "flete")) {
            return "🚚 Opciones de entrega:\n\n" +
                   "🏪 Recojo en tienda (Sin costo)\n" +
                   "   📍 Pachacutec, Carretera de Tate, Ica\n" +
                   "   ⏰ Lunes a Sábado: 8am - 6pm\n\n" +
                   "🚚 Envío a domicilio\n" +
                   "   📍 Cobertura en la región de Ica\n" +
                   "   ⏰ Entrega en 24-48 horas\n\n" +
                   "📞 Para más información: 342-444-0263";
        }

        // =====================
        // HORARIO
        // =====================
        if (contiene(p, "horario", "hora", "cuando abren", "cuando cierran", "atienden",
                "horarios de atencion", "horario de atencion", "que horas", "a que hora",
                "abierto", "cerrado", "dias de atencion", "trabajan", "laborable")) {
            return "⏰ Horario de atención:\n\n" +
                   "• Lunes a Viernes: 8:00am - 6:00pm\n" +
                   "• Sábado: 8:00am - 2:00pm\n" +
                   "• Domingo: Cerrado\n\n" +
                   "📱 Pedidos online: Las 24 horas\n" +
                   "📞 Consultas: 342-444-0263";
        }

        // =====================
        // CONTACTO / UBICACIÓN
        // =====================
        if (contiene(p, "contacto", "telefono", "llamar", "whatsapp", "direccion", "ubicacion",
                "donde estan", "donde quedan", "como llego", "como los contacto",
                "numero de telefono", "correo", "email", "redes sociales", "instagram",
                "facebook", "informacion de contacto", "datos de contacto")) {
            return "📞 Información de contacto:\n\n" +
                   "• 📱 Teléfono: 342-444-0263\n" +
                   "• 📧 Email: pedidos@distribuidorasanmartin.com\n" +
                   "• 📍 Dirección: Pachacutec, Carretera de Tate, Ica\n" +
                   "• ⏰ Horario: Lun-Vie 8am-6pm | Sáb 8am-2pm\n\n" +
                   "¡Estamos para ayudarte! 😊";
        }

        // =====================
        // DEVOLUCIONES / GARANTÍA
        // =====================
        if (contiene(p, "devolucion", "cambio", "garantia", "reclamo", "queja", "problema",
                "defecto", "falla", "no funciona", "esta malo", "esta roto", "producto malo",
                "politica de devolucion", "como devuelvo", "quiero devolver", "reembolso")) {
            return "🔄 Política de devoluciones y garantía:\n\n" +
                   "• ⏰ Plazo: 7 días desde la compra\n" +
                   "• 📦 El producto debe estar en estado original\n" +
                   "• 🧾 Presenta tu número de pedido\n" +
                   "• 📞 Contáctanos: 342-444-0263\n\n" +
                   "Tipos de solución:\n" +
                   "• Cambio por el mismo producto\n" +
                   "• Nota de crédito\n" +
                   "• Devolución del dinero\n\n" +
                   "¡Tu satisfacción es nuestra prioridad! 😊";
        }

        // =====================
        // REGISTRO / CUENTA
        // =====================
        if (contiene(p, "registro", "registrarme", "crear cuenta", "nueva cuenta", "como me registro",
                "como creo mi cuenta", "quiero registrarme", "cuenta nueva", "usuario nuevo",
                "como inicio sesion", "inicio de sesion", "login", "entrar", "acceder",
                "olvide mi contrasena", "olvide contrasena", "recuperar contrasena")) {
            return "👤 Gestión de cuenta:\n\n" +
                   "📝 Para registrarte:\n" +
                   "1. Haz clic en 'Registrarse'\n" +
                   "2. Ingresa tu nombre completo\n" +
                   "3. Crea un nombre de usuario\n" +
                   "4. Ingresa tu DNI y celular\n" +
                   "5. Crea una contraseña segura\n\n" +
                   "🔐 Para iniciar sesión:\n" +
                   "• Usa tu nombre de usuario y contraseña\n\n" +
                   "¿Tienes algún problema? 📞 342-444-0263";
        }

        // =====================
        // COMPROBANTE / BOLETA
        // =====================
        if (contiene(p, "comprobante", "boleta", "factura", "recibo", "documento",
                "constancia de pago", "prueba de compra", "ticket", "voucher")) {
            return "🧾 Comprobante de compra:\n\n" +
                   "Al confirmar tu pedido puedes:\n" +
                   "• 📄 Descargar tu comprobante en PDF\n" +
                   "• El comprobante incluye:\n" +
                   "  - Número de pedido\n" +
                   "  - Datos del cliente (nombre y DNI)\n" +
                   "  - Productos comprados\n" +
                   "  - Subtotal, IGV y total\n" +
                   "  - Método de pago y tipo de entrega\n\n" +
                   "El comprobante queda disponible en 'Mis Pedidos'.";
        }

        // =====================
        // QUIÉNES SOMOS / SOBRE LA EMPRESA
        // =====================
        if (contiene(p, "quienes son", "sobre ustedes", "empresa", "distribuidora",
                "que es san martin", "informacion de la empresa", "historia", "acerca de",
                "que hacen", "a que se dedican", "mision", "vision")) {
            return "🏪 Sobre Distribuidora San Martín:\n\n" +
                   "Somos una empresa dedicada a la venta y distribución de productos de calidad.\n\n" +
                   "✅ Importadores directos\n" +
                   "✅ Precios preferenciales\n" +
                   "✅ Envíos a toda la región\n" +
                   "✅ Atención personalizada\n\n" +
                   "📍 Pachacutec, Carretera de Tate, Ica\n" +
                   "📞 342-444-0263\n" +
                   "📧 pedidos@distribuidorasanmartin.com";
        }

        // =====================
        // DESCUENTOS / PROMOCIONES
        // =====================
        if (contiene(p, "descuento", "descuentos", "promocion", "promociones", "oferta especial",
                "precio especial", "rebaja", "sale", "black friday", "cyber", "cupon",
                "codigo de descuento", "hay promociones", "tienen descuentos")) {
            return "🎉 Promociones disponibles:\n\n" +
                   "• 🛒 Compras mayoristas con descuento especial\n" +
                   "• 📦 Consulta por compras al por mayor\n\n" +
                   "Para más información sobre promociones especiales:\n" +
                   "📞 Llámanos al 342-444-0263\n" +
                   "📧 pedidos@distribuidorasanmartin.com";
        }

        // =====================
        // CUÁNTOS PRODUCTOS TIENEN
        // =====================
        if (contiene(p, "cuantos productos", "cuanto productos", "total de productos",
                "numero de productos", "cantidad de productos")) {
            return "📦 Tenemos " + todos.size() + " productos disponibles en nuestro catálogo.\n\n" +
                   "Escríbeme el nombre de un producto para ver sus detalles, " +
                   "o pregúntame por una categoría específica.";
        }

        // =====================
        // BÚSQUEDA EN BD — debe ir siempre al final
        // =====================
        List<String> claves = extraerPalabrasClave(p);
        List<Producto> encontrados = new ArrayList<>();

        for (Producto prod : todos) {
            String nombre = normalizar(prod.getNombreProducto());
            String desc = prod.getDescripcion() != null ? normalizar(prod.getDescripcion()) : "";
            for (String clave : claves) {
                if (clave.length() > 2 && (nombre.contains(clave) || desc.contains(clave))) {
                    if (!encontrados.contains(prod)) encontrados.add(prod);
                    break;
                }
            }
        }

        if (!encontrados.isEmpty()) {
            StringBuilder sb = new StringBuilder("🔍 Encontré lo siguiente:\n\n");
            for (Producto prod : encontrados) {
                sb.append("📦 ").append(prod.getNombreProducto()).append("\n");
                sb.append("   💰 Precio: S/. ").append(prod.getPrecioUnitario()).append("\n");
                sb.append("   📊 Stock: ").append(prod.getStockActual()).append(" unidades");
                if (prod.getStockActual() == 0) sb.append(" ❌ Agotado");
                else if (prod.getStockActual() <= prod.getStockMinimo()) sb.append(" ⚠️ Stock bajo");
                else sb.append(" ✅ Disponible");
                if (prod.getDescripcion() != null && !prod.getDescripcion().isBlank()) {
                    sb.append("\n   📝 ").append(prod.getDescripcion());
                }
                sb.append("\n\n");
            }
            return sb.toString();
        }

        // =====================
        // RESPUESTA POR DEFECTO
        // =====================
        return "🤔 No encontré información sobre eso.\n\n" +
               "Puedes preguntarme sobre:\n" +
               "• 📦 Productos: '¿tienen laptops?', '¿precio del iPhone?'\n" +
               "• 📊 Stock: '¿qué productos están disponibles?'\n" +
               "• 🏷️ Categorías: '¿qué categorías tienen?'\n" +
               "• 🛒 Pedidos: '¿cómo hago un pedido?'\n" +
               "• 💳 Pagos: '¿qué métodos de pago aceptan?'\n" +
               "• 🚚 Envíos: '¿hacen delivery?'\n" +
               "• 📞 Contacto: '¿cuál es su número?'\n\n" +
               "O llámanos directamente: 📞 342-444-0263";
    }

    private boolean contiene(String texto, String... palabras) {
        for (String palabra : palabras) {
            if (texto.contains(palabra)) return true;
        }
        return false;
    }

    private List<String> extraerPalabrasClave(String pregunta) {
        String[] stopWords = {
            "tienen", "hay", "tiene", "cuanto", "cuesta", "vale", "precio",
            "stock", "disponible", "queda", "quedan", "el", "la", "los", "las",
            "un", "una", "de", "del", "me", "puedo", "ver", "que", "como",
            "donde", "cuando", "por", "para", "con", "sin", "sobre", "entre",
            "algún", "alguna", "busco", "quiero", "necesito", "marca", "marcas",
            "producto", "productos", "modelo", "modelos", "tipo", "tipos",
            "muestren", "muestrame", "dime", "buscar", "encontrar", "quisiera",
            "podrian", "pueden", "podria", "seria", "es", "son", "sera", "al",
            "del", "les", "sus", "hay", "mas", "muy", "mucho", "muchos",
            "poco", "pocos", "alguno", "algunos", "todo", "todos", "nada"
        };

        List<String> resultado = new ArrayList<>();
        for (String palabra : pregunta.split("\\s+")) {
            palabra = palabra.trim();
            if (palabra.length() <= 2) continue;
            boolean esStop = false;
            for (String stop : stopWords) {
                if (palabra.equals(stop)) { esStop = true; break; }
            }
            if (!esStop) resultado.add(palabra);
        }
        return resultado;
    }
}