package com.fantasyfightleague.repository;

import com.fantasyfightleague.model.Event;
import com.fantasyfightleague.model.Fight;
import com.fantasyfightleague.model.Fighter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FightRepository extends JpaRepository<Fight, Long> {
    
    // Buscar peleas por evento
    List<Fight> findByEvent(Event event);
    
    // Buscar peleas donde participa un luchador
    List<Fight> findByFighter1OrFighter2(Fighter fighter1, Fighter fighter2);
    
    // Buscar peleas principales
    List<Fight> findByIsMainEventTrue();
    
    // Buscar peleas por categoría de peso
    List<Fight> findByWeightClass(String weightClass);
    
    // Buscar peleas por estado
    List<Fight> findByStatus(String status);
    
    // Buscar peleas de un evento por estado
    List<Fight> findByEventAndStatus(Event event, String status);
}