package com.fantasyfightleague.repository;

import com.fantasyfightleague.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    // Buscar eventos futuros
    List<Event> findByDateAfter(Date date);
    
    // Buscar eventos pasados
    List<Event> findByDateBefore(Date date);
    
    // Buscar por nombre (búsqueda parcial)
    List<Event> findByNameContainingIgnoreCase(String name);
    
    // Buscar por estado
    List<Event> findByStatus(String status);
    
    // Buscar eventos próximos (ordenados por fecha ascendente)
    List<Event> findByDateAfterOrderByDateAsc(Date date);
}