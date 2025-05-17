package com.fantasyfightleague.service;

import com.fantasyfightleague.model.Fighter;

import java.util.List;
import java.util.Optional;

public interface FighterService {
    
    // Guardar un luchador
    Fighter saveFighter(Fighter fighter);
    
    // Buscar un luchador por su ID
    Optional<Fighter> findById(Long id);
    
    // Buscar luchadores por categoría de peso
    List<Fighter> findByWeightClass(String weightClass);
    
    // Buscar luchadores por nombre (búsqueda parcial)
    List<Fighter> findByName(String name);
    
    // Buscar luchadores por rango de precio
    List<Fighter> findByPriceRange(Integer minPrice, Integer maxPrice);
    
    // Obtener todos los luchadores
    List<Fighter> findAllFighters();
    
    // Obtener todos los luchadores activos
    List<Fighter> findAllActiveFighters();
    
    // Eliminar un luchador
    void deleteFighter(Long id);
    
    // Actualizar el precio de un luchador
    Fighter updatePrice(Long id, Integer price);
    
    // Marcar un luchador como inactivo
    Fighter deactivateFighter(Long id);
}