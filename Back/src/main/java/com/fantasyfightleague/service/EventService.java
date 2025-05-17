package com.fantasyfightleague.service;

import com.fantasyfightleague.model.Event;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {
    
    // Guardar un evento
    Event saveEvent(Event event);
    
    // Buscar un evento por su ID
    Optional<Event> findById(Long id);
    
    // Buscar eventos futuros
    List<Event> findUpcomingEvents();
    
    // Buscar eventos pasados
    List<Event> findPastEvents();
    
    // Buscar eventos por nombre
    List<Event> findByName(String name);
    
    // Buscar eventos por estado
    List<Event> findByStatus(String status);
    
    // Buscar el próximo evento
    Optional<Event> findNextEvent();
    
    // Obtener todos los eventos
    List<Event> findAllEvents();
    
    // Eliminar un evento
    void deleteEvent(Long id);
    
    // Cambiar el estado de un evento
    Event updateStatus(Long id, String status);
}