// PickService.java
package com.fantasyfightleague.service;

import com.fantasyfightleague.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PickService {
    
    // Crear o actualizar pick
    Pick createOrUpdatePick(User user, League league, Event event, List<Long> fighterIds);
    
    // Buscar picks
    Optional<Pick> findById(Long id);
    Optional<Pick> findByUserLeagueAndEvent(User user, League league, Event event);
    List<Pick> findByUserAndLeague(User user, League league);
    List<Pick> findByLeagueAndEvent(League league, Event event);
    
    // Clasificaciones
    List<Pick> getEventLeaderboard(League league, Event event);
    List<Map<String, Object>> getGlobalLeaderboard(League league);
    
    // Gestión de picks
    void deletePick(Long pickId);
    int lockPicksForEvent(Event event);
    void lockExpiredPicks(); // Para scheduler automático
    
    // Validaciones
    boolean canUserMakePick(User user, League league, Event event);
    boolean isValidPickCombination(List<Fighter> fighters, Integer budget);
}