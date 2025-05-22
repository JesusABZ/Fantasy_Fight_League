// Crear: src/main/java/com/fantasyfightleague/service/LeagueService.java
package com.fantasyfightleague.service;

import com.fantasyfightleague.model.Event;
import com.fantasyfightleague.model.League;
import com.fantasyfightleague.model.User;

import java.util.List;
import java.util.Optional;

public interface LeagueService {
    
    // Crear ligas
    League createPublicLeague(String name, String description, Event event, User creator);
    League createPrivateLeague(String name, String description, User creator);
    
    // Buscar ligas
    Optional<League> findById(Long id);
    List<League> findPublicLeagues();
    List<League> findActivePublicLeagues();
    List<League> findPrivateLeaguesByCreator(User creator);
    List<League> findLeaguesByUser(User user);
    Optional<League> findByInvitationCode(String invitationCode);
    
    // Gestión de miembros
    League joinLeague(League league, User user);
    League leaveLeague(League league, User user);
    boolean isUserInLeague(League league, User user);
    
    // Gestión de ligas
    void deleteLeague(Long id);
    void deleteExpiredPublicLeagues();
    List<League> findExpiredPublicLeagues();
    
    // Utilidades
    String generateInvitationCode();
    boolean isValidInvitationCode(String code);
}