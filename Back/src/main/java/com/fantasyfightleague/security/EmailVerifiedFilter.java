// Crear este archivo en: src/main/java/com/fantasyfightleague/security/EmailVerifiedFilter.java
package com.fantasyfightleague.security;

import com.fantasyfightleague.model.User;
import com.fantasyfightleague.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger; // ✅ IMPORT CORRECTO
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class EmailVerifiedFilter extends OncePerRequestFilter {
    
   
    
    private static final Logger logger = LoggerFactory.getLogger(EmailVerifiedFilter.class);
    
    private final UserService userService;
    
    // ✅ AGREGAR CONSTRUCTOR
    public EmailVerifiedFilter(UserService userService) {
        this.userService = userService;
    }
    
    // Endpoints que requieren email verificado
    private final List<String> PROTECTED_ENDPOINTS = Arrays.asList(
        "/api/leagues",
        "/api/picks",
        "/api/admin"
    );
    
    // Endpoints que NO requieren email verificado
    private final List<String> EXCLUDED_ENDPOINTS = Arrays.asList(
        "/api/auth",
        "/api/user/profile",
        "/api/user/change-email",
        "/api/events",
        "/api/support"
    );
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // Solo aplicar filtro a métodos que modifican datos
        if (!"POST".equals(method) && !"PUT".equals(method) && !"DELETE".equals(method)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Verificar si es un endpoint excluido
        boolean isExcluded = EXCLUDED_ENDPOINTS.stream()
                .anyMatch(requestURI::startsWith);
        
        if (isExcluded) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Verificar si es un endpoint protegido
        boolean isProtected = PROTECTED_ENDPOINTS.stream()
                .anyMatch(requestURI::startsWith);
        
        if (!isProtected) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Verificar autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Verificar email confirmado
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
            
            if (userOpt.isPresent() && !userOpt.get().isEmailConfirmed()) {
                // Email no verificado - devolver error
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "EMAIL_NOT_VERIFIED");
                errorResponse.put("message", "Debes verificar tu email antes de realizar esta acción");
                errorResponse.put("userEmail", userOpt.get().getEmail());
                
                ObjectMapper mapper = new ObjectMapper();
                response.getWriter().write(mapper.writeValueAsString(errorResponse));
                return;
            }
        } catch (Exception e) {
            // En caso de error, continuar con el filtro normal
            logger.warn("Error verificando estado de email: " + e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
}