package com.distribuidora.sanmartin.controllers;

import com.distribuidora.sanmartin.models.Producto;
import com.distribuidora.sanmartin.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    private ProductoService productoService;

    @PostMapping("/consultar")
    public Map<String, String> consultar(@RequestBody Map<String, String> body) {
        String pregunta = body.get("pregunta");

        // Construir contexto con productos reales de la BD
        List<Producto> productos = productoService.listarProductos();
        StringBuilder contexto = new StringBuilder();
        contexto.append("Eres el asistente virtual de Distribuidora San Martín. ");
        contexto.append("Solo responde preguntas relacionadas con los productos, stock, precios y pedidos de la tienda. ");
        contexto.append("Estos son los productos disponibles actualmente:\n\n");

        for (Producto p : productos) {
            contexto.append("- ").append(p.getNombreProducto())
                    .append(" | Precio: S/. ").append(p.getPrecioUnitario())
                    .append(" | Stock: ").append(p.getStockActual()).append(" unidades");
            if (p.getDescripcion() != null && !p.getDescripcion().isBlank()) {
                contexto.append(" | Descripción: ").append(p.getDescripcion());
            }
            contexto.append("\n");
        }
        contexto.append("\nPregunta del cliente: ").append(pregunta);
        contexto.append("\nResponde de forma breve, amigable y en español.");

        // Llamar a Gemini API
        WebClient client = WebClient.create();
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiApiKey;

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", contexto.toString())
                ))
            )
        );

        try {
            Map response = client.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            List candidates = (List) response.get("candidates");
            Map candidate = (Map) candidates.get(0);
            Map content = (Map) candidate.get("content");
            List parts = (List) content.get("parts");
            Map part = (Map) parts.get(0);
            String respuesta = (String) part.get("text");

            return Map.of("respuesta", respuesta);
        } catch (Exception e) {
            return Map.of("respuesta", "Lo siento, no pude procesar tu consulta en este momento. Intenta de nuevo.");
        }
    }
}