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
        // NO ENTENDIÓ / RESPUESTA INCORRECTA
        // =====================
        if (contiene(p, "no dije eso", "no es eso", "no era eso", "no pregunte eso",
                "no pregunté eso", "no entendiste", "no me entendiste", "no entendio",
                "no entendió", "esa no es la respuesta", "no tiene nada que ver",
                "no es lo que busco", "no es lo que pregunte", "no es lo que pregunté",
                "eso no tiene sentido", "no es correcto", "estas mal", "estás mal",
                "te equivocaste", "no es asi", "no es así", "mal entendido")) {
            return "Disculpa, creo que no entendí bien tu consulta. 😅\n\n" +
                   "¿Podrías reformular tu pregunta? Por ejemplo:\n" +
                   "• '¿Qué me recomiendas comprar?'\n" +
                   "• '¿Tienen laptops disponibles?'\n" +
                   "• '¿Cuál es el producto más económico?'\n\n" +
                   "Intentaré ayudarte mejor esta vez 🙏";
        }

        // =====================
        // RECOMENDACIONES (va antes que "pedido" porque ambas comparten
        // la palabra "comprar" y esta es mas especifica)
        // =====================
        if (contiene(p, "recomienda", "recomiendan", "recomiendas", "recomendacion", "recomendaciones",
                "recomiendame", "recomiéndame", "que me sugieren", "que sugieren", "sugerencia", "sugerencias",
                "que compro", "que llevo", "que compraria", "que compraría", "que deberia comprar",
                "que debería comprar", "que deberia llevar", "que producto me recomiendas",
                "que productos me recomiendas", "que me recomiendas", "que me recomiendas comprar",
                "que es lo mejor", "lo mejor", "mas vendido", "mas vendidos", "popular", "populares",
                "favorito", "cual me conviene", "cual me recomiendas", "cual recomiendas")) {
            List<Producto> conStock = todos.stream()
                .filter(pr -> pr.getStockActual() > pr.getStockMinimo())
                .collect(Collectors.toList());
            if (conStock.isEmpty()) return "Por el momento no tenemos productos con stock disponible.";
            StringBuilder sb = new StringBuilder("⭐ Te recomendamos estos productos:\n\n");
            int limite = Math.min(3, conStock.size());
            for (int i = 0; i < limite; i++) {
                Producto pr = conStock.get(i);
                sb.append("📦 ").append(pr.getNombreProducto()).append("\n");
                sb.append("   💰 Precio: S/. ").append(pr.getPrecioUnitario()).append("\n");
                sb.append("   📊 Stock: ").append(pr.getStockActual()).append(" unidades ✅\n\n");
            }
            sb.append("¿Te interesa alguno de estos productos?");
            return sb.toString();
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
                   "  📍 Pachacutec, Carretera de Tate, Ica\n" +
                   "  ⏰ Lunes a Sábado: 8am - 6pm\n\n" +
                   "🚚 Envío a domicilio\n" +
                   "  📍 Cobertura en la región de Ica\n" +
                   "  ⏰ Entrega en 24-48 horas\n\n" +
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
        // PRECIO DE PRODUCTO ESPECÍFICO
        // =====================
        if (contiene(p, "cuanto cuesta", "cual es el precio", "precio de", "cuanto vale",
                "a cuanto esta", "cuanto es", "precio del", "cuanto sale")) {
            List<String> clavesPrecio = extraerPalabrasClave(p);
            List<Producto> encontradosPrecio = new ArrayList<>();
            for (Producto prod : todos) {
                String nombre = normalizar(prod.getNombreProducto());
                for (String clave : clavesPrecio) {
                    if (clave.length() > 2 && nombre.contains(clave)) {
                        if (!encontradosPrecio.contains(prod)) encontradosPrecio.add(prod);
                        break;
                    }
                }
            }
            if (!encontradosPrecio.isEmpty()) {
                StringBuilder sb = new StringBuilder("💰 Información de precios:\n\n");
                for (Producto prod : encontradosPrecio) {
                    sb.append("📦 ").append(prod.getNombreProducto()).append("\n");
                    sb.append("   💵 Precio: S/. ").append(prod.getPrecioUnitario()).append("\n");
                    sb.append("   📊 Stock: ").append(prod.getStockActual()).append(" unidades\n\n");
                }
                return sb.toString();
            }
        }

        // =====================
        // COMPARAR PRECIOS
        // =====================
        if (contiene(p, "mas barato de", "menos caro de", "mejor precio de",
                "cuanto cuesta el mas barato", "precio minimo de")) {
            return todos.stream()
                .min(Comparator.comparing(Producto::getPrecioUnitario))
                .map(pr -> "💰 El más económico:\n\n📦 " + pr.getNombreProducto() +
                           "\n💵 S/. " + pr.getPrecioUnitario())
                .orElse("No hay productos.");
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
        Map<String, String> sinonimos = construirSinonimos();

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
            if (palabra.isEmpty()) continue;

            if (sinonimos.containsKey(palabra)) {
                String base = sinonimos.get(palabra);
                if (!base.isEmpty() && !resultado.contains(base)) {
                    resultado.add(base);
                }
                continue;
            }

            if (palabra.length() <= 2) continue;
            boolean esStop = false;
            for (String stop : stopWords) {
                if (palabra.equals(stop)) { esStop = true; break; }
            }
            if (!esStop && !resultado.contains(palabra)) resultado.add(palabra);
        }
        return resultado;
    }

    private Map<String, String> construirSinonimos() {
        Map<String, String> sinonimos = new HashMap<>();
        sinonimos.put("iphones", "iphone");
        sinonimos.put("iphone", "iphone");
        sinonimos.put("apple", "iphone");
        sinonimos.put("celular", "");
        sinonimos.put("celulares", "");
        sinonimos.put("smartphones", "");
        sinonimos.put("smartphone", "");
        sinonimos.put("telefono", "");
        sinonimos.put("telefonos", "");
        sinonimos.put("laptop", "laptop");
        sinonimos.put("laptops", "laptop");
        sinonimos.put("computadora", "laptop");
        sinonimos.put("computadoras", "laptop");
        sinonimos.put("computador", "laptop");
        sinonimos.put("computadores", "laptop");
        sinonimos.put("pc", "laptop");
        sinonimos.put("notebook", "laptop");
        sinonimos.put("notebooks", "laptop");
        sinonimos.put("asus", "asus");
        sinonimos.put("samsung", "samsung");
        sinonimos.put("tv", "tv");
        sinonimos.put("tvs", "tv");
        sinonimos.put("television", "tv");
        sinonimos.put("televisor", "tv");
        sinonimos.put("televisores", "tv");
        sinonimos.put("televisiones", "tv");
        sinonimos.put("smart tv", "smart");
        sinonimos.put("smarttv", "smart");
        sinonimos.put("refrigerador", "refrigerador");
        sinonimos.put("refrigeradora", "refrigerador");
        sinonimos.put("refrigeradoras", "refrigerador");
        sinonimos.put("refrigeradores", "refrigerador");
        sinonimos.put("fridge", "refrigerador");
        sinonimos.put("nevera", "refrigerador");
        sinonimos.put("neveras", "refrigerador");
        sinonimos.put("heladera", "refrigerador");
        sinonimos.put("auricular", "auricular");
        sinonimos.put("auriculares", "auricular");
        sinonimos.put("audifonos", "audifono");
        sinonimos.put("audifono", "audifono");
        sinonimos.put("headphones", "audifono");
        sinonimos.put("airpods", "airpods");
        sinonimos.put("airpod", "airpods");
        sinonimos.put("lavadora", "lavadora");
        sinonimos.put("lavadoras", "lavadora");
        sinonimos.put("microondas", "microondas");
        sinonimos.put("horno", "horno");
        sinonimos.put("hornos", "horno");
        sinonimos.put("licuadora", "licuadora");
        sinonimos.put("licuadoras", "licuadora");
        sinonimos.put("aspiradora", "aspiradora");
        sinonimos.put("aspiradoras", "aspiradora");
        sinonimos.put("ventilador", "ventilador");
        sinonimos.put("ventiladores", "ventilador");
        sinonimos.put("parlante", "parlante");
        sinonimos.put("parlantes", "parlante");
        sinonimos.put("bocina", "parlante");
        sinonimos.put("bocinas", "parlante");
        sinonimos.put("speaker", "parlante");
        sinonimos.put("tablet", "tablet");
        sinonimos.put("tablets", "tablet");
        sinonimos.put("ipad", "tablet");
        sinonimos.put("reloj", "reloj");
        sinonimos.put("relojes", "reloj");
        sinonimos.put("smartwatch", "reloj");
        sinonimos.put("camara", "camara");
        sinonimos.put("camaras", "camara");
        sinonimos.put("impresora", "impresora");
        sinonimos.put("impresoras", "impresora");
        sinonimos.put("monitor", "monitor");
        sinonimos.put("monitores", "monitor");
        sinonimos.put("teclado", "teclado");
        sinonimos.put("teclados", "teclado");
        sinonimos.put("mouse", "mouse");
        sinonimos.put("mouses", "mouse");
        sinonimos.put("router", "router");
        sinonimos.put("routers", "router");
        sinonimos.put("disco", "disco");
        sinonimos.put("discos", "disco");
        sinonimos.put("memoria", "memoria");
        sinonimos.put("memorias", "memoria");
        sinonimos.put("cable", "cable");
        sinonimos.put("cables", "cable");
        sinonimos.put("cargador", "cargador");
        sinonimos.put("cargadores", "cargador");
        return sinonimos;
    }
}