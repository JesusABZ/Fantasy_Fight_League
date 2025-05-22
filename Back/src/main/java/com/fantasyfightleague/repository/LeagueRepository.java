// Actualizar: src/main/java/com/fantasyfightleague/repository/LeagueRepository.java
package com.fantasyfightleague.repository;

import com.fantasyfightleague.model.League;
import com.fantasyfightleague.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {
    
    // Buscar ligas por tipo
    List<League> findByType(String type);
    List<League> findByTypeAndActiveTrue(String type);
    
    // Buscar ligas activas por tipo con fecha de eliminación
    List<League> findByTypeAndActiveTrueAndAutoDeleteDateAfter(String type, Date date);
    List<League> findByTypeAndActiveTrueAndAutoDeleteDateBefore(String type, Date date);
    
    // Buscar ligas creadas por un usuario
    List<League> findByCreator(User creator);
    List<League> findByCreatorAndTypeAndActiveTrue(User creator, String type);
    
    // Buscar ligas activas
    List<League> findByActiveTrue();
    
    // Buscar por código de invitación
    League findByInvitationCode(String invitationCode);
    League findByInvitationCodeAndActiveTrue(String invitationCode);
    
    // Buscar ligas públicas asociadas a un evento
    List<League> findByTypeAndEventId(String type, Long eventId);
    
    // Buscar ligas donde un usuario es miembro
    List<League> findByMembersContaining(User user);
}