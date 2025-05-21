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
}