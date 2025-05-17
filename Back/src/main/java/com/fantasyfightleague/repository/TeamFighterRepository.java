package com.fantasyfightleague.repository;

import com.fantasyfightleague.model.Fighter;
import com.fantasyfightleague.model.Team;
import com.fantasyfightleague.model.TeamFighter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamFighterRepository extends JpaRepository<TeamFighter, Long> {
    
    // Buscar todos los luchadores de un equipo
    List<TeamFighter> findByTeam(Team team);
    
    // Verificar si un luchador ya está en un equipo
    Optional<TeamFighter> findByTeamAndFighter(Team team, Fighter fighter);
    
    // Buscar luchadores seleccionados para el evento
    List<TeamFighter> findByTeamAndSelectedForEventTrue(Team team);
    
    // Contar cuántos luchadores tiene un equipo
    long countByTeam(Team team);
    
    // Contar cuántos luchadores están seleccionados para el evento
    long countByTeamAndSelectedForEventTrue(Team team);
}