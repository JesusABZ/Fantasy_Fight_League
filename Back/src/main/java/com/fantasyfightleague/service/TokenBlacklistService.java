// Back/src/main/java/com/fantasyfightleague/service/TokenBlacklistService.java
package com.fantasyfightleague.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    
    // En memoria - para producción usar Redis o base de datos
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    
    /**
     * Agregar token a la lista negra
     */
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }
    
    /**
     * Verificar si un token está en la lista negra
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
    
    /**
     * Limpiar tokens expirados (llamar periódicamente)
     */
    public void cleanupExpiredTokens() {
        // Implementar lógica para limpiar tokens expirados
        // Por ahora, limpiar todo cada cierto tiempo
        blacklistedTokens.clear();
    }
}