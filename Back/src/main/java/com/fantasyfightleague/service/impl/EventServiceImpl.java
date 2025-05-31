package com.fantasyfightleague.service.impl;

import com.fantasyfightleague.controller.AdminController;
import com.fantasyfightleague.model.Event;
import com.fantasyfightleague.repository.EventRepository;
import com.fantasyfightleague.service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {
    
    private final EventRepository eventRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    
    @Override
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }
    
    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }
    
    @Override
    public List<Event> findUpcomingEvents() {
        // ✅ CORREGIDO: Usar startDate en lugar de date
        return eventRepository.findByStartDateAfter(new Date());
    }
    
    @Override
    public List<Event> findPastEvents() {
        // ✅ CORREGIDO: Usar startDate en lugar de date
        return eventRepository.findByStartDateBefore(new Date());
    }
    
    @Override
    public List<Event> findByName(String name) {
        return eventRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    public List<Event> findByStatus(String status) {
        return eventRepository.findByStatus(status);
    }
    
    @Override
    public Optional<Event> findNextEvent() {
        // ✅ CORREGIDO: Usar startDate en lugar de date
        List<Event> upcomingEvents = eventRepository.findByStartDateAfterOrderByStartDateAsc(new Date());
        if (upcomingEvents.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(upcomingEvents.get(0));
    }
    
    @Override
    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }
    
    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
    
    @Override
    public Event updateStatus(Long id, String status) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        event.setStatus(status);
        return eventRepository.save(event);
    }
    
    @Override
    public Optional<Event> findPreviousEvent() {
        try {
            // Obtener todos los eventos ordenados por fecha descendente
            List<Event> allEvents = eventRepository.findByStartDateAfterOrderByStartDateAsc(new Date(0)); // Desde 1970 para obtener todos
            
            if (allEvents.isEmpty()) {
                return Optional.empty();
            }
            
            // Ordenar por fecha descendente (más reciente primero)
            allEvents.sort((a, b) -> {
                Date dateA = a.getStartDate() != null ? a.getStartDate() : a.getDate();
                Date dateB = b.getStartDate() != null ? b.getStartDate() : b.getDate();
                return dateB.compareTo(dateA);
            });
            
            Date now = new Date();
            
            // Buscar el evento actual (próximo o en progreso)
            Event currentEvent = null;
            int currentEventIndex = -1;
            
            for (int i = 0; i < allEvents.size(); i++) {
                Event event = allEvents.get(i);
                Date eventDate = event.getStartDate() != null ? event.getStartDate() : event.getDate();
                
                // Si el evento es futuro o está en progreso, es el evento actual
                if (eventDate.after(now) || "LIVE".equals(event.getStatus()) || "UPCOMING".equals(event.getStatus())) {
                    currentEvent = event;
                    currentEventIndex = i;
                    break;
                }
            }
            
            // Si no hay evento actual, tomar el más reciente como actual
            if (currentEvent == null && !allEvents.isEmpty()) {
                currentEventIndex = 0;
            }
            
            // El evento anterior es el siguiente en la lista (índice + 1)
            if (currentEventIndex >= 0 && currentEventIndex < allEvents.size() - 1) {
                return Optional.of(allEvents.get(currentEventIndex + 1));
            }
            
            return Optional.empty();
            
        } catch (Exception e) {
            logger.error("Error al buscar evento anterior: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<Event> findAllEventsOrderByDate() {
        try {
            List<Event> allEvents = eventRepository.findAll();
            
            // Ordenar por fecha descendente (más reciente primero)
            allEvents.sort((a, b) -> {
                Date dateA = a.getStartDate() != null ? a.getStartDate() : a.getDate();
                Date dateB = b.getStartDate() != null ? b.getStartDate() : b.getDate();
                if (dateA == null && dateB == null) return 0;
                if (dateA == null) return 1;
                if (dateB == null) return -1;
                return dateB.compareTo(dateA);
            });
            
            return allEvents;
            
        } catch (Exception e) {
            logger.error("Error al obtener eventos ordenados: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Event> getCurrentAndPreviousEvents() {
        Map<String, Event> events = new HashMap<>();
        
        try {
            // Obtener todos los eventos ordenados por fecha
            List<Event> allEvents = findAllEventsOrderByDate();
            
            if (allEvents.isEmpty()) {
                return events;
            }
            
            Date now = new Date();
            Event currentEvent = null;
            Event previousEvent = null;
            
            // Buscar evento actual
            for (int i = 0; i < allEvents.size(); i++) {
                Event event = allEvents.get(i);
                Date eventDate = event.getStartDate() != null ? event.getStartDate() : event.getDate();
                
                if (eventDate == null) continue;
                
                // Si el evento es futuro o está en progreso, es el evento actual
                if (eventDate.after(now) || "LIVE".equals(event.getStatus()) || "UPCOMING".equals(event.getStatus())) {
                    currentEvent = event;
                    
                    // El evento anterior es el siguiente en la lista
                    if (i < allEvents.size() - 1) {
                        previousEvent = allEvents.get(i + 1);
                    }
                    break;
                }
            }
            
            // Si no hay evento futuro, el más reciente es el "actual"
            if (currentEvent == null && !allEvents.isEmpty()) {
                currentEvent = allEvents.get(0);
                if (allEvents.size() > 1) {
                    previousEvent = allEvents.get(1);
                }
            }
            
            if (currentEvent != null) {
                events.put("current", currentEvent);
            }
            
            if (previousEvent != null) {
                events.put("previous", previousEvent);
            }
            
            logger.info("Eventos encontrados - Actual: {}, Anterior: {}", 
                       currentEvent != null ? currentEvent.getName() : "Ninguno",
                       previousEvent != null ? previousEvent.getName() : "Ninguno");
            
        } catch (Exception e) {
            logger.error("Error al obtener eventos actual y anterior: {}", e.getMessage(), e);
        }
        
        return events;
    }
}