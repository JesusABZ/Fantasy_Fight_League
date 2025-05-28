// Back/src/main/java/com/fantasyfightleague/service/PasswordResetService.java
package com.fantasyfightleague.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fantasyfightleague.model.PasswordResetToken;
import com.fantasyfightleague.model.User;
import com.fantasyfightleague.repository.PasswordResetTokenRepository;
import com.fantasyfightleague.repository.UserRepository;

import jakarta.mail.internet.MimeMessage;

@Service
public class PasswordResetService {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    
    @Autowired
    private PasswordResetTokenRepository resetTokenRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${app.baseUrl}")
    private String baseUrl;
    
    /**
     * Envía un email de recuperación de contraseña
     */
    @Transactional
    public void sendPasswordResetEmail(String email) {
        logger.info("Solicitud de recuperación de contraseña para: {}", email);
        
        // 1. Buscar usuario por email
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (!userOpt.isPresent()) {
            // Por seguridad, no revelar si el email existe o no
            logger.warn("Intento de recuperación para email no existente: {}", email);
            return; // Salir silenciosamente
        }
        
        User user = userOpt.get();
        
        // 2. Eliminar tokens anteriores del usuario
        resetTokenRepository.deleteByUserId(user.getId());
        
        // 3. Crear nuevo token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        resetTokenRepository.save(resetToken);
        
        // 4. Enviar email
        try {
            sendResetPasswordEmail(user, token);
            logger.info("Email de recuperación enviado exitosamente a: {}", email);
        } catch (Exception e) {
            logger.error("Error al enviar email de recuperación: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar el email de recuperación");
        }
    }
    
    /**
     * Valida un token de reset
     */
    public PasswordResetToken validateResetToken(String token) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(token);
        
        if (resetToken == null) {
            throw new RuntimeException("Token de recuperación inválido");
        }
        
        if (resetToken.isUsed()) {
            throw new RuntimeException("Este enlace ya ha sido utilizado");
        }
        
        if (resetToken.isExpired()) {
            throw new RuntimeException("El enlace de recuperación ha expirado");
        }
        
        return resetToken;
    }
    
    /**
     * Cambia la contraseña usando un token de reset
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        logger.info("Cambiando contraseña con token: {}", token);
        
        // 1. Validar token
        PasswordResetToken resetToken = validateResetToken(token);
        User user = resetToken.getUser();
        
        // 2. Cambiar la contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // 3. Marcar token como usado
        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
        
        // 4. Marcar todos los otros tokens del usuario como usados
        resetTokenRepository.markTokensAsUsedForUser(user.getId());
        
        logger.info("Contraseña cambiada exitosamente para usuario: {}", user.getUsername());
        
        // 5. Opcional: Enviar email de confirmación
        try {
            sendPasswordChangedConfirmation(user);
        } catch (Exception e) {
            logger.warn("No se pudo enviar email de confirmación de cambio: {}", e.getMessage());
            // No lanzar excepción ya que lo importante (cambio de contraseña) ya se hizo
        }
    }
    
    /**
     * Envía el email de recuperación de contraseña
     */
    private void sendResetPasswordEmail(User user, String token) throws Exception {
        String resetUrl = baseUrl + "/reset-password?token=" + token;
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(user.getEmail());
        helper.setSubject("Fantasy Fight League - Recuperar Contraseña");
        
        String htmlContent = buildResetPasswordEmailContent(user, resetUrl);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
    
    /**
     * Construye el contenido HTML del email de recuperación
     */
    private String buildResetPasswordEmailContent(User user, String resetUrl) {
        return "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
               "<div style='background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;'>" +
               "<h1 style='color: white; margin: 0; font-size: 28px; font-weight: bold;'>🔑 RECUPERAR CONTRASEÑA</h1>" +
               "</div>" +
               "<div style='background: #1a1a1a; color: white; padding: 30px; border-radius: 0 0 10px 10px;'>" +
               "<p style='font-size: 16px; margin-bottom: 20px; color: white;'>Hola <strong>" + user.getUsername() + "</strong>,</p>" +
               "<p style='font-size: 16px; margin-bottom: 20px; color: white;'>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en Fantasy Fight League.</p>" +
               "<p style='font-size: 16px; margin-bottom: 30px; color: white;'>Si no solicitaste este cambio, puedes ignorar este email. Tu contraseña no será modificada.</p>" +
               "<div style='text-align: center; margin: 30px 0;'>" +
               "<a href='" + resetUrl + "' style='background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%); color: white !important; padding: 15px 30px; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 16px; display: inline-block;'>CAMBIAR CONTRASEÑA</a>" +
               "</div>" +
               "<p style='font-size: 14px; color: #b0b0b0; margin-top: 30px;'>Este enlace expirará en 24 horas por seguridad.</p>" +
               "<p style='font-size: 14px; color: #b0b0b0;'>Si tienes problemas con el botón, copia y pega este enlace en tu navegador:</p>" +
               "<p style='font-size: 12px; color: #4a9eff !important; word-break: break-all; text-decoration: none;'>" + resetUrl + "</p>" +
               "<hr style='border: none; border-top: 1px solid #333; margin: 30px 0;'>" +
               "<p style='font-size: 12px; color: #888; text-align: center;'>Fantasy Fight League<br>¡Que gane el mejor fighter!</p>" +
               "</div>" +
               "</div>";
    }
    
    /**
     * Envía email de confirmación de cambio de contraseña
     */
    private void sendPasswordChangedConfirmation(User user) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(user.getEmail());
        helper.setSubject("Fantasy Fight League - Contraseña Cambiada");
        
        String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                            "<div style='background: linear-gradient(135deg, #10b981 0%, #059669 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;'>" +
                            "<h1 style='color: white; margin: 0; font-size: 28px; font-weight: bold;'>✅ CONTRASEÑA CAMBIADA</h1>" +
                            "</div>" +
                            "<div style='background: #1a1a1a; color: white; padding: 30px; border-radius: 0 0 10px 10px;'>" +
                            "<p style='font-size: 16px; margin-bottom: 20px;'>Hola <strong>" + user.getUsername() + "</strong>,</p>" +
                            "<p style='font-size: 16px; margin-bottom: 20px;'>Tu contraseña ha sido cambiada exitosamente.</p>" +
                            "<p style='font-size: 16px; margin-bottom: 20px;'>Si no fuiste tú quien hizo este cambio, contacta inmediatamente con nuestro soporte.</p>" +
                            "<div style='text-align: center; margin: 30px 0;'>" +
                            "<a href='" + baseUrl + "/login' style='background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%); color: white; padding: 15px 30px; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 16px; display: inline-block;'>INICIAR SESIÓN</a>" +
                            "</div>" +
                            "<hr style='border: none; border-top: 1px solid #333; margin: 30px 0;'>" +
                            "<p style='font-size: 12px; color: #888; text-align: center;'>Fantasy Fight League<br>¡Que gane el mejor fighter!</p>" +
                            "</div>" +
                            "</div>";
        
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
    
    /**
     * Limpia tokens expirados (para tarea programada)
     */
    @Transactional
    public void cleanupExpiredTokens() {
        resetTokenRepository.deleteExpiredTokens(new java.util.Date());
        logger.info("Tokens de recuperación expirados eliminados");
    }
}