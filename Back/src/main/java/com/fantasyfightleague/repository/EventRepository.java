package com.fantasyfightleague.repository;

import com.fantasyfightleague.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    // ✅ MÉTODOS ORIGINALES (para compatibilidad con date)
    List<Event> findByDateAfter(Date date);
    List<Event> findByDateBefore(Date date);
    List<Event> findByDateAfterOrderByDateAsc(Date date);
    
    // ✅ NUEVOS MÉTODOS usando startDate
    List<Event> findByStartDateAfter(Date date);
    List<Event> findByStartDateBefore(Date date);
    List<Event> findByStartDateAfterOrderByStartDateAsc(Date date);
    
    // Métodos comunes que no cambian
    List<Event> findByNameContainingIgnoreCase(String name);
    List<Event> findByStatus(String status);
}