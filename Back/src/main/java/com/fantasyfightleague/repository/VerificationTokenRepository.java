// Crear este archivo en: src/main/java/com/fantasyfightleague/repository/VerificationTokenRepository.java
package com.fantasyfightleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fantasyfightleague.model.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}