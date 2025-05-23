// Crear este archivo en: src/main/java/com/fantasyfightleague/service/EmailVerificationService.java
package com.fantasyfightleague.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
    
    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        createVerificationToken(user, token);
        
        String confirmationUrl = baseUrl + "/api/auth/confirm?token=" + token;
        
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Fantasy Fight League - Confirmación de Email");
        email.setText("Hola " + user.getUsername() + ",\n\n"
                + "¡Gracias por registrarte en Fantasy Fight League! Por favor, confirma tu dirección de correo electrónico haciendo clic en el siguiente enlace:\n\n"
                + confirmationUrl + "\n\n"
                + "Si no te has registrado en nuestra plataforma, por favor ignora este correo.\n\n"
                + "Saludos,\nEl equipo de Fantasy Fight League");
        
        mailSender.send(email);
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
    
    public void sendEmailChangeVerification(User user, String oldEmail) {
        String token = UUID.randomUUID().toString();
        
        // Eliminar token anterior si existe
        VerificationToken existingToken = tokenRepository.findByToken(token);
        if (existingToken != null) {
            tokenRepository.delete(existingToken);
        }
        
        createVerificationToken(user, token);
        
        String confirmationUrl = baseUrl + "/api/auth/confirm?token=" + token;
        
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail()); // Enviar al nuevo email
        email.setSubject("Fantasy Fight League - Confirmación de Cambio de Email");
        email.setText("Hola " + user.getUsername() + ",\n\n"
                + "Hemos recibido una solicitud para cambiar tu dirección de correo electrónico.\n\n"
                + "Email anterior: " + oldEmail + "\n"
                + "Nuevo email: " + user.getEmail() + "\n\n"
                + "Para confirmar este cambio, haz clic en el siguiente enlace:\n\n"
                + confirmationUrl + "\n\n"
                + "Si no has solicitado este cambio, contacta inmediatamente con nuestro soporte.\n\n"
                + "Saludos,\nEl equipo de Fantasy Fight League");
        
        mailSender.send(email);
        
        // Opcional: Enviar notificación al email anterior
        SimpleMailMessage notificationEmail = new SimpleMailMessage();
        notificationEmail.setTo(oldEmail);
        notificationEmail.setSubject("Fantasy Fight League - Cambio de Email Solicitado");
        notificationEmail.setText("Hola " + user.getUsername() + ",\n\n"
                + "Se ha solicitado un cambio de email para tu cuenta.\n\n"
                + "Si no has sido tú, contacta inmediatamente con nuestro soporte.\n\n"
                + "Saludos,\nEl equipo de Fantasy Fight League");
        
        mailSender.send(notificationEmail);
    }
}