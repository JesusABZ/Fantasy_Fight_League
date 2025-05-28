// Actualizar: src/main/java/com/fantasyfightleague/service/EmailVerificationService.java
package com.fantasyfightleague.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.fantasyfightleague.model.User;
import com.fantasyfightleague.model.VerificationToken;
import com.fantasyfightleague.repository.VerificationTokenRepository;

@Service
public class EmailVerificationService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private VerificationTokenRepository tokenRepository;
    
    @Value("${app.baseUrl}")
    private String baseUrl;
    
    @Transactional
    public void sendVerificationEmail(User user) {
        // ✅ ELIMINAR TOKENS EXISTENTES ANTES DE CREAR UNO NUEVO
        deleteExistingTokensForUser(user);
        
        String token = UUID.randomUUID().toString();
        createVerificationToken(user, token);
        
        String confirmationUrl = baseUrl + "/confirm-email?token=" + token;
        
//        //String confirmationUrl = baseUrl + "/api/auth/confirm?token=" + token;
//        
//        SimpleMailMessage email = new SimpleMailMessage();
//        email.setTo(user.getEmail());
//        email.setSubject("Fantasy Fight League - Confirmación de Email");
//        email.setText("Hola " + user.getUsername() + ",\n\n"
//                + "¡Gracias por registrarte en Fantasy Fight League! Por favor, confirma tu dirección de correo electrónico haciendo clic en el siguiente enlace:\n\n"
//                + confirmationUrl + "\n\n"
//                + "Si no te has registrado en nuestra plataforma, por favor ignora este correo.\n\n"
//                + "Saludos,\nEl equipo de Fantasy Fight League");
//        
//        mailSender.send(email);
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("Fantasy Fight League - Confirmación de Email");

            String htmlContent = "<p>Hola <strong>" + user.getUsername() + "</strong>,</p>"
                    + "<p>¡Gracias por registrarte en Fantasy Fight League! Por favor, confirma tu dirección de correo electrónico haciendo clic en el siguiente enlace:</p>"
                    + "<p><a href=\"" + confirmationUrl + "\">Confirmar mi correo electrónico</a></p>"
                    + "<p>Si no te has registrado en nuestra plataforma, por favor ignora este correo.</p>"
                    + "<p>Saludos,<br>El equipo de Fantasy Fight League</p>";

            helper.setText(htmlContent, true); // true para HTML

            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Error enviando correo: " + e.getMessage());
        }
    }
    
    @Transactional
    public void sendEmailChangeVerification(User user, String oldEmail) {
        // ✅ ELIMINAR TOKENS EXISTENTES ANTES DE CREAR UNO NUEVO
        deleteExistingTokensForUser(user);
        
        String token = UUID.randomUUID().toString();
        createVerificationToken(user, token);
        
        //String confirmationUrl = baseUrl + "/api/auth/confirm?token=" + token;
        String confirmationUrl = baseUrl + "/confirm-email?token=" + token;
        
//        // Email al nuevo email
//        SimpleMailMessage email = new SimpleMailMessage();
//        email.setTo(user.getEmail()); // Enviar al nuevo email
//        email.setSubject("Fantasy Fight League - Confirmación de Cambio de Email");
//        email.setText("Hola " + user.getUsername() + ",\n\n"
//                + "Hemos recibido una solicitud para cambiar tu dirección de correo electrónico.\n\n"
//                + "Email anterior: " + oldEmail + "\n"
//                + "Nuevo email: " + user.getEmail() + "\n\n"
//                + "Para confirmar este cambio, haz clic en el siguiente enlace:\n\n"
//                + confirmationUrl + "\n\n"
//                + "Si no has solicitado este cambio, contacta inmediatamente con nuestro soporte.\n\n"
//                + "Saludos,\nEl equipo de Fantasy Fight League");
//        
//        mailSender.send(email);
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail()); // Solo se envía al nuevo email
            helper.setSubject("Fantasy Fight League - Confirmación de Cambio de Email");

            String htmlContent = "<p>Hola <strong>" + user.getUsername() + "</strong>,</p>"
                    + "<p>Hemos recibido una solicitud para cambiar tu dirección de correo electrónico.</p>"
                    + "<p>Email anterior: <strong>" + oldEmail + "</strong><br>"
                    + "Nuevo email: <strong>" + user.getEmail() + "</strong></p>"
                    + "<p>Para confirmar este cambio, haz clic en el siguiente enlace:</p>"
                    + "<p><a href=\"" + confirmationUrl + "\">Confirmar cambio de correo electrónico</a></p>"
                    + "<p>Si no has solicitado este cambio, contacta inmediatamente con nuestro soporte.</p>"
                    + "<p>Saludos,<br>El equipo de Fantasy Fight League</p>";

            helper.setText(htmlContent, true); // `true` para HTML

            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Error enviando correo de verificación de cambio de email: " + e.getMessage());
        }
        
        // Opcional: Enviar notificación al email anterior
        try {
            SimpleMailMessage notificationEmail = new SimpleMailMessage();
            notificationEmail.setTo(oldEmail);
            notificationEmail.setSubject("Fantasy Fight League - Cambio de Email Solicitado");
            notificationEmail.setText("Hola " + user.getUsername() + ",\n\n"
                    + "Se ha solicitado un cambio de email para tu cuenta.\n\n"
                    + "Nuevo email: " + user.getEmail() + "\n\n"
                    + "Si no has sido tú, contacta inmediatamente con nuestro soporte.\n\n"
                    + "Saludos,\nEl equipo de Fantasy Fight League");
            
            mailSender.send(notificationEmail);
        } catch (Exception e) {
            // Si falla el envío al email anterior, continuar sin error
            System.out.println("No se pudo enviar notificación al email anterior: " + e.getMessage());
        }
    }
    
    /**
     * ✅ NUEVO MÉTODO: Elimina todos los tokens existentes para un usuario
     */
    @Transactional
    private void deleteExistingTokensForUser(User user) {
        try {
            // Usar el método del repositorio para eliminar tokens existentes
            tokenRepository.deleteByUserId(user.getId());
        } catch (Exception e) {
            // Si hay error, continuar sin problema
            System.out.println("Error eliminando tokens existentes: " + e.getMessage());
        }
    }
    
    private void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken();
        myToken.setToken(token);
        myToken.setUser(user);
        tokenRepository.save(myToken);
    }
    
    public VerificationToken getVerificationToken(String token) {
        return tokenRepository.findByToken(token);
    }
}