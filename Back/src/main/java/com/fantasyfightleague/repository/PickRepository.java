package com.fantasyfightleague.repository;

import com.fantasyfightleague.model.Event;
import com.fantasyfightleague.model.League;
import com.fantasyfightleague.model.Pick;
import com.fantasyfightleague.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PickRepository extends JpaRepository<Pick, Long> {
    
    // Buscar pick de un usuario en una liga y evento específico
    Optional<Pick> findByUserAndLeagueAndEvent(User user, League league, Event event);
    
    // Buscar todos los picks de un usuario
    List<Pick> findByUser(User user);
    
    // Buscar todos los picks de una liga para un evento
    List<Pick> findByLeagueAndEvent(League league, Event event);
    
    // Buscar picks de una liga ordenados por puntos (clasificación por evento)
    List<Pick> findByLeagueAndEventOrderByEventPointsDesc(League league, Event event);
    
    // Buscar picks de un usuario en una liga (todos los eventos)
    List<Pick> findByUserAndLeague(User user, League league);
    
    // Clasificación global por liga (suma de puntos de todos los eventos)
    @Query("SELECT p.user, SUM(p.eventPoints) as totalPoints FROM Pick p " +
           "WHERE p.league = ?1 " +
           "GROUP BY p.user " +
           "ORDER BY totalPoints DESC")
    List<Object[]> findGlobalLeaderboard(League league);
    
    // Verificar si un usuario ya tiene pick en un evento de una liga
    boolean existsByUserAndLeagueAndEvent(User user, League league, Event event);
    
    // Contar picks en una liga para un evento
    long countByLeagueAndEvent(League league, Event event);
    
    // Buscar picks que deben ser bloqueados (pasó el deadline)
    @Query("SELECT p FROM Pick p WHERE p.isLocked = false AND p.event.picksDeadline < CURRENT_TIMESTAMP")
    List<Pick> findPicksToLock();
}