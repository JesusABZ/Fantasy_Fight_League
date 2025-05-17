package com.fantasyfightleague.repository;

import com.fantasyfightleague.model.League;
import com.fantasyfightleague.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {
    
    // Buscar ligas por tipo (pública o privada)
    List<League> findByType(String type);
    
    // Buscar ligas creadas por un usuario
    List<League> findByCreator(User creator);
    
    // Buscar ligas activas
    List<League> findByActiveTrue();
    
    // Buscar ligas por código de invitación
    League findByInvitationCode(String invitationCode);
    
    // Buscar ligas públicas asociadas a un evento
    List<League> findByTypeAndEventId(String type, Long eventId);
    
    // Buscar ligas donde un usuario es miembro
    @Query("SELECT l FROM League l JOIN l.members m WHERE m.id = :userId")
    List<League> findByMemberId(Long userId);
}