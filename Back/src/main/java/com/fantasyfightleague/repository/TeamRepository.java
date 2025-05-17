package com.fantasyfightleague.repository;

import com.fantasyfightleague.model.League;
import com.fantasyfightleague.model.Team;
import com.fantasyfightleague.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    // Buscar equipos por usuario
    List<Team> findByUser(User user);
    
    // Buscar equipos por liga
    List<Team> findByLeague(League league);
    
    // Buscar equipos por usuario y liga
    Optional<Team> findByUserAndLeague(User user, League league);
    
    // Buscar equipos ordenados por puntos totales (para clasificación global)
    List<Team> findByLeagueOrderByTotalPointsDesc(League league);
    
    // Buscar equipos ordenados por puntos del evento (para clasificación del evento)
    List<Team> findByLeagueOrderByEventPointsDesc(League league);
    
    // Contar el número de equipos en una liga
    long countByLeague(League league);
}