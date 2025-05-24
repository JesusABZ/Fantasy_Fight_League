// Actualizar: src/main/java/com/fantasyfightleague/repository/VerificationTokenRepository.java
package com.fantasyfightleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fantasyfightleague.model.User;
import com.fantasyfightleague.model.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    
    VerificationToken findByToken(String token);
    
    // ✅ NUEVO: Buscar token por usuario
    VerificationToken findByUser(User user);
    
    // ✅ NUEVO: Eliminar tokens por usuario
    @Modifying
    @Transactional
    @Query("DELETE FROM VerificationToken vt WHERE vt.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    // ✅ NUEVO: Verificar si existe token para usuario
    boolean existsByUser(User user);
}