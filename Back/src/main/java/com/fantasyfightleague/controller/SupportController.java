// Crear: src/main/java/com/fantasyfightleague/controller/SupportController.java
package com.fantasyfightleague.controller;

import com.fantasyfightleague.dto.MessageResponseDTO;
import com.fantasyfightleague.dto.SupportTicketDTO;
import com.fantasyfightleague.service.SupportService;
import com.fantasyfightleague.service.UserService;
import com.fantasyfightleague.model.User;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/support")
public class SupportController {
    
    private static final Logger logger = LoggerFactory.getLogger(SupportController.class);
    
    @Autowired
    private SupportService supportService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Crear un ticket de soporte - Accesible para usuarios autenticados y no autenticados
     */
    @PostMapping("/ticket")
    public ResponseEntity<?> createSupportTicket(@Valid @RequestBody SupportTicketDTO ticket, 
                                                 HttpServletRequest request) {
        try {
            // Intentar obtener información del usuario si está autenticado
            try {
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal instanceof UserDetails) {
                    UserDetails userDetails = (UserDetails) principal;
                    Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
                    
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        // Si el usuario está autenticado pero no proporcionó email, usar el del perfil
                        if (ticket.getEmail() == null || ticket.getEmail().trim().isEmpty()) {
                            ticket.setEmail(user.getEmail());
                        }
                        // Si no proporcionó username, usar el del usuario autenticado
                        if (ticket.getUsername() == null || ticket.getUsername().trim().isEmpty()) {
                            ticket.setUsername(user.getUsername());
                        }
                    }
                }
            } catch (Exception e) {
                // Si hay error obteniendo el usuario autenticado, continuar sin él
                logger.debug("Usuario no autenticado o error obteniendo datos: {}", e.getMessage());
            }
            
            // Obtener información del navegador
            String userAgent = request.getHeader("User-Agent");
            if (userAgent != null) {
                ticket.setUserAgent(userAgent);
            }
            
            // Validar el ticket
            supportService.validateTicket(ticket);
            
            // Procesar el ticket
            String ticketReference = supportService.processSupportTicket(ticket);
            
            // Crear respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("ticketReference", ticketReference);
            response.put("message", "Tu ticket de soporte ha sido enviado correctamente. " +
                                   "Recibirás un email de confirmación con la referencia #" + ticketReference);
            
            logger.info("Ticket de soporte creado exitosamente: {} por usuario: {}", 
                       ticketReference, ticket.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación en ticket de soporte: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error de validación: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al crear ticket de soporte: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new MessageResponseDTO("Error interno del servidor. Por favor, inténtalo más tarde."));
        }
    }
    
    /**
     * Obtener categorías disponibles para tickets de soporte
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getSupportCategories() {
        Map<String, String> categories = new HashMap<>();
        categories.put("BUG", "Reportar un Error");
        categories.put("ACCOUNT_ISSUE", "Problema con la Cuenta");
        categories.put("SCORING_ISSUE", "Problema con Puntuaciones");
        categories.put("LEAGUE_ISSUE", "Problema con Ligas");
        categories.put("FEATURE_REQUEST", "Sugerencia de Mejora");
        categories.put("PAYMENT_ISSUE", "Problema de Pago");
        categories.put("GENERAL", "Consulta General");
        categories.put("OTHER", "Otro");
        
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Endpoint para verificar el estado del sistema de soporte
     */
    @GetMapping("/status")
    public ResponseEntity<?> getSupportStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("available", true);
        status.put("responseTime", "24-48 horas");
        status.put("supportEmail", "soporte@fantasyfightleague.com");
        status.put("languages", new String[]{"es", "en"});
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * Endpoint para obtener información de contacto (público)
     */
    @GetMapping("/contact-info")
    public ResponseEntity<?> getContactInfo() {
        Map<String, Object> contactInfo = new HashMap<>();
        contactInfo.put("supportEmail", "soporte@fantasyfightleague.com");
        contactInfo.put("responseTime", "Responderemos en un plazo de 24-48 horas");
        contactInfo.put("availableLanguages", new String[]{"Español", "English"});
        contactInfo.put("faq", "Consulta nuestro centro de ayuda en la aplicación");
        contactInfo.put("tips", new String[]{
            "Incluye toda la información relevante en tu mensaje",
            "Si reportas un error, describe los pasos para reproducirlo",
            "Para problemas de puntuación, incluye el nombre del evento y luchadores",
            "Guarda el número de referencia para futuras consultas"
        });
        
        return ResponseEntity.ok(contactInfo);
    }
}