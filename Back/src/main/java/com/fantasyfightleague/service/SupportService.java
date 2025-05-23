// Crear: src/main/java/com/fantasyfightleague/service/SupportService.java
package com.fantasyfightleague.service;

import com.fantasyfightleague.dto.SupportTicketDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Servicio para gestionar tickets de soporte
 */
@Service
public class SupportService {
    
    private static final Logger logger = LoggerFactory.getLogger(SupportService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.support.email:jexuxgta69@gmail.com}")
    private String supportEmail;
    
    @Value("${app.support.auto-reply:true}")
    private boolean autoReplyEnabled;
    
    /**
     * Procesa un ticket de soporte
     * @param ticket Datos del ticket
     * @return Número de referencia del ticket
     */
    public String processSupportTicket(SupportTicketDTO ticket) {
        String ticketReference = generateTicketReference();
        
        try {
            // 1. Enviar email al equipo de soporte (a ti)
            sendSupportNotification(ticket, ticketReference);
            
            // 2. Enviar email de confirmación automática al usuario
            if (autoReplyEnabled) {
                sendAutoReply(ticket, ticketReference);
            }
            
            logger.info("Ticket de soporte procesado: {} - Usuario: {} - Email: {}", 
                       ticketReference, ticket.getUsername(), ticket.getEmail());
            
            return ticketReference;
            
        } catch (Exception e) {
            logger.error("Error al procesar ticket de soporte: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar el ticket de soporte: " + e.getMessage());
        }
    }
    
    /**
     * Genera un número de referencia único para el ticket
     */
    private String generateTicketReference() {
        Random random = new Random();
        int reference = 100000 + random.nextInt(900000); // Número de 6 dígitos
        return String.valueOf(reference);
    }
    
    /**
     * Envía notificación al equipo de soporte
     */
    private void sendSupportNotification(SupportTicketDTO ticket, String ticketReference) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(supportEmail);
        message.setSubject("[SOPORTE FFL #" + ticketReference + "] " + ticket.getSubject());
        
        StringBuilder body = new StringBuilder();
        body.append("🎫 NUEVO TICKET DE SOPORTE\n");
        body.append("═══════════════════════════════════════\n\n");
        body.append("📋 INFORMACIÓN DEL TICKET:\n");
        body.append("• Referencia: #").append(ticketReference).append("\n");
        body.append("• Fecha: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
        body.append("• Categoría: ").append(ticket.getCategory() != null ? ticket.getCategory() : "GENERAL").append("\n\n");
        
        body.append("👤 INFORMACIÓN DEL USUARIO:\n");
        body.append("• Email: ").append(ticket.getEmail()).append("\n");
        body.append("• Username: ").append(ticket.getUsername() != null ? ticket.getUsername() : "No registrado").append("\n");
        if (ticket.getUserAgent() != null) {
            body.append("• Navegador: ").append(ticket.getUserAgent()).append("\n");
        }
        body.append("\n");
        
        body.append("📝 ASUNTO:\n");
        body.append(ticket.getSubject()).append("\n\n");
        
        body.append("💬 MENSAJE:\n");
        body.append(ticket.getMessage()).append("\n\n");
        
        body.append("═══════════════════════════════════════\n");
        body.append("Para responder, usa la referencia: #").append(ticketReference).append("\n");
        body.append("Fantasy Fight League - Sistema de Soporte\n");
        
        message.setText(body.toString());
        
        // Responder al email del usuario por defecto
        message.setReplyTo(ticket.getEmail());
        
        mailSender.send(message);
        logger.info("Email de notificación enviado al soporte para ticket: {}", ticketReference);
    }
    
    /**
     * Envía respuesta automática al usuario
     */
    private void sendAutoReply(SupportTicketDTO ticket, String ticketReference) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(ticket.getEmail());
        message.setSubject("Confirmación de ticket - Fantasy Fight League #" + ticketReference);
        
        StringBuilder body = new StringBuilder();
        body.append("##- Por favor, escriba su respuesta por encima de esta línea -##\n\n");
        
        body.append("¡Hola fighter!\n\n");
        
        body.append("Confirmamos haber recibido tu solicitud con la referencia (#").append(ticketReference).append("). ");
        body.append("Nuestro equipo de soporte se pondrá en contacto contigo a la mayor brevedad posible para darte una respuesta. ");
        body.append("En cualquier caso, si deseas enviarnos nueva información por correo electrónico, puedes hacerlo respondiendo a este mensaje.\n\n");
        
        body.append("📋 RESUMEN DE TU CONSULTA:\n");
        body.append("• Asunto: ").append(ticket.getSubject()).append("\n");
        body.append("• Categoría: ").append(ticket.getCategory() != null ? ticket.getCategory() : "General").append("\n");
        body.append("• Fecha: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");
        
        body.append("💡 CONSEJOS ÚTILES:\n");
        body.append("* Te recomendamos que revises nuestro centro de ayuda disponible en la aplicación.\n");
        body.append("* Si tu consulta es sobre puntuaciones, recuerda que se actualizan después de cada evento.\n");
        body.append("* Para problemas de acceso, verifica que tu email esté confirmado.\n\n");
        
        body.append("¡Gracias por contactar con nosotros!\n");
        body.append("Fantasy Fight League\n\n");
        
        body.append("═══════════════════════════════════════\n\n");
        
        body.append("Hey fighter!\n\n");
        
        body.append("We confirm that we have received your request with the reference (#").append(ticketReference).append("). ");
        body.append("Our support team will contact you as soon as possible to give you an answer. ");
        body.append("In any case, if you wish to send us new information by email, you can do so by replying to this message.\n\n");
        
        body.append("Cheers!\n");
        body.append("Fantasy Fight League\n");
        
        message.setText(body.toString());
        
        mailSender.send(message);
        logger.info("Email de confirmación automática enviado a: {} para ticket: {}", ticket.getEmail(), ticketReference);
    }
    
    /**
     * Valida el ticket antes de procesarlo
     */
    public void validateTicket(SupportTicketDTO ticket) {
        if (ticket.getEmail() == null || ticket.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        
        if (ticket.getSubject() == null || ticket.getSubject().trim().isEmpty()) {
            throw new IllegalArgumentException("El asunto es obligatorio");
        }
        
        if (ticket.getMessage() == null || ticket.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje es obligatorio");
        }
        
        if (ticket.getSubject().length() > 200) {
            throw new IllegalArgumentException("El asunto no puede tener más de 200 caracteres");
        }
        
        if (ticket.getMessage().length() > 2000) {
            throw new IllegalArgumentException("El mensaje no puede tener más de 2000 caracteres");
        }
    }
}