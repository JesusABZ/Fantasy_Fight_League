package com.fantasyfightleague.repository;

import com.fantasyfightleague.model.Fight;
import com.fantasyfightleague.model.Fighter;
import com.fantasyfightleague.model.FighterStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FighterStatsRepository extends JpaRepository<FighterStats, Long> {
    
    // Buscar estadísticas de un luchador en una pelea
    Optional<FighterStats> findByFighterAndFight(Fighter fighter, Fight fight);
    
    // Buscar todas las estadísticas de un luchador
    List<FighterStats> findByFighter(Fighter fighter);
    
    // Buscar estadísticas de una pelea
    List<FighterStats> findByFight(Fight fight);
    
    // Buscar estadísticas ordenadas por puntos (descendente)
    List<FighterStats> findByFightOrderByPointsDesc(Fight fight);
    
    // Obtener la suma de puntos de un luchador
    @Query("SELECT SUM(fs.points) FROM FighterStats fs WHERE fs.fighter.id = :fighterId")
    Integer getTotalPointsByFighterId(Long fighterId);
}