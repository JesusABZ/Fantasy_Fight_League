// Actualizar: src/main/java/com/fantasyfightleague/controller/EventController.java
package com.fantasyfightleague.controller;

import com.fantasyfightleague.dto.CreateEventDTO;
import com.fantasyfightleague.dto.MessageResponseDTO;
import com.fantasyfightleague.model.Event;
import com.fantasyfightleague.model.Fighter;
import com.fantasyfightleague.service.EventService;
import com.fantasyfightleague.service.FighterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private FighterService fighterService;
    
    /**
     * SOLO ADMINS - Crear evento CON luchadores activos automáticamente
     */
    @PostMapping("/admin/events")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEvent(@RequestBody CreateEventDTO eventDTO) {
        try {
            // Crear el evento
            Event event = new Event();
            event.setName(eventDTO.getName());
            event.setDescription(eventDTO.getDescription());
            event.setLocation(eventDTO.getLocation());
            event.setImageUrl(eventDTO.getImageUrl());
            event.setStartDate(eventDTO.getStartDate());
            event.setEndDate(eventDTO.getEndDate());
            
            // Guardar el evento primero
            Event savedEvent = eventService.saveEvent(event);
            
            // Obtener todos los luchadores activos y agregarlos al evento
            List<Fighter> activeFighters = fighterService.findAllActiveFighters();
            for (Fighter fighter : activeFighters) {
                savedEvent.addFighter(fighter);
            }
            
            // Guardar el evento actualizado con los luchadores
            savedEvent = eventService.saveEvent(savedEvent);
            
            return ResponseEntity.ok(savedEvent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
    
    /**
     * PÚBLICO - Obtener todos los eventos
     */
    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.findAllEvents();
        return ResponseEntity.ok(events);
    }
    
    /**
     * PÚBLICO - Obtener eventos próximos
     */
    @GetMapping("/events/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        List<Event> events = eventService.findUpcomingEvents();
        return ResponseEntity.ok(events);
    }
    
    /**
     * PÚBLICO - Obtener evento por ID con sus luchadores
     */
    @GetMapping("/events/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        return eventService.findById(id)
                .map(event -> ResponseEntity.ok(event))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * PÚBLICO - Obtener luchadores de un evento
     */
    @GetMapping("/events/{id}/fighters")
    public ResponseEntity<?> getEventFighters(@PathVariable Long id) {
        return eventService.findById(id)
                .map(event -> ResponseEntity.ok(event.getFighters()))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * SOLO ADMINS - Actualizar estado del evento
     */
    @PutMapping("/admin/events/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEventStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Event event = eventService.updateStatus(id, status);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
    /**
     * PÚBLICO - Obtener el próximo evento
     */
    @GetMapping("/events/next")
    public ResponseEntity<?> getNextEvent() {
        return eventService.findNextEvent()
                .map(event -> ResponseEntity.ok(event))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * PÚBLICO - Obtener el evento anterior
     */
    @GetMapping("/events/previous")
    public ResponseEntity<?> getPreviousEvent() {
        return eventService.findPreviousEvent()
                .map(event -> ResponseEntity.ok(event))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PÚBLICO - Obtener evento actual y anterior juntos
     */
    @GetMapping("/events/current-and-previous")
    public ResponseEntity<?> getCurrentAndPreviousEvents() {
        try {
            Map<String, Event> events = eventService.getCurrentAndPreviousEvents();
            
            Map<String, Object> response = new HashMap<>();
            response.put("current", events.get("current"));
            response.put("previous", events.get("previous"));
            response.put("hasCurrentEvent", events.containsKey("current"));
            response.put("hasPreviousEvent", events.containsKey("previous"));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }

    /**
     * PÚBLICO - Obtener todos los eventos ordenados por fecha
     */
    @GetMapping("/events/ordered")
    public ResponseEntity<List<Event>> getAllEventsOrdered() {
        try {
            List<Event> events = eventService.findAllEventsOrderByDate();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ArrayList<>());
        }
    }
}