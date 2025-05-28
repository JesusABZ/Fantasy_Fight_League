// Back/src/main/java/com/fantasyfightleague/repository/PasswordResetTokenRepository.java
package com.fantasyfightleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fantasyfightleague.model.PasswordResetToken;
import com.fantasyfightleague.model.User;

import java.util.Date;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    PasswordResetToken findByToken(String token);
    
    // Buscar token por usuario
    PasswordResetToken findByUser(User user);
    
    // Eliminar tokens por usuario
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    // Verificar si existe token para usuario
    boolean existsByUser(User user);
    
    // Eliminar tokens expirados
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") Date now);
    
    // Marcar tokens como usados para un usuario
    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetToken prt SET prt.used = true WHERE prt.user.id = :userId AND prt.used = false")
    void markTokensAsUsedForUser(@Param("userId") Long userId);
}