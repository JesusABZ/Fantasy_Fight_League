// Back/src/main/java/com/fantasyfightleague/repository/EventRepository.java
// AGREGAR estos métodos al EventRepository existente

package com.fantasyfightleague.repository;

import com.fantasyfightleague.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    // ✅ MÉTODOS ORIGINALES (para compatibilidad con date)
    List<Event> findByDateAfter(Date date);
    List<Event> findByDateBefore(Date date);
    List<Event> findByDateAfterOrderByDateAsc(Date date);
    
    // ✅ MÉTODOS EXISTENTES usando startDate
    List<Event> findByStartDateAfter(Date date);
    List<Event> findByStartDateBefore(Date date);
    List<Event> findByStartDateAfterOrderByStartDateAsc(Date date);
    
    // 🆕 NUEVOS MÉTODOS para eventos anteriores
    @Query("SELECT e FROM Event e WHERE e.startDate IS NOT NULL ORDER BY e.startDate DESC")
    List<Event> findAllOrderByStartDateDesc();
    
    @Query("SELECT e FROM Event e WHERE e.startDate IS NOT NULL ORDER BY e.startDate ASC")
    List<Event> findAllOrderByStartDateAsc();
    
    @Query("SELECT e FROM Event e WHERE e.startDate < :date ORDER BY e.startDate DESC")
    List<Event> findPastEventsOrderByStartDateDesc(Date date);
    
    @Query("SELECT e FROM Event e WHERE e.startDate >= :date ORDER BY e.startDate ASC")
    List<Event> findFutureEventsOrderByStartDateAsc(Date date);
    
    // Métodos comunes que no cambian
    List<Event> findByNameContainingIgnoreCase(String name);
    List<Event> findByStatus(String status);
    
    // 🆕 NUEVO: Buscar eventos por estado ordenados por fecha
    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.startDate IS NOT NULL ORDER BY e.startDate DESC")
    List<Event> findByStatusOrderByStartDateDesc(String status);
}