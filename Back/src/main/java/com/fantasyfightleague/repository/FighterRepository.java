package com.fantasyfightleague.repository;

import com.fantasyfightleague.model.Fighter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FighterRepository extends JpaRepository<Fighter, Long> {
    
    // Buscar luchadores por categoría de peso
    List<Fighter> findByWeightClass(String weightClass);
    
    // Buscar luchadores por nombre (búsqueda parcial)
    List<Fighter> findByNameContainingIgnoreCase(String name);
    
    // Buscar luchadores por rango de precio
    List<Fighter> findByPriceBetween(Integer minPrice, Integer maxPrice);
    
    // Buscar luchadores activos
    List<Fighter> findByActiveTrue();
}