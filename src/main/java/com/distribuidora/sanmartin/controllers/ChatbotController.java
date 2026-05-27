package com.distribuidora.sanmartin.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*") // Permite que la ventanita de chat se conecte sin bloqueos
public class ChatbotController {

    // Ruta que recibirá las preguntas del cliente en la web
    @PostMapping("/consultar")
    public String procesarConsultaChatbot(@RequestBody String mensajeCliente) {
        
        // En el futuro, aquí conectaremos el SDK de Gemini o tu microservicio de FastAPI
        
        System.out.println("Consulta recibida en el sistema: " + mensajeCliente);
        
        return "¡Hola! Soy el asistente virtual de Distribuidora San Martín. " +
               "Pronto estaré conectado con Gemini AI para ayudarte con el stock y precios en tiempo real.";
    }
}