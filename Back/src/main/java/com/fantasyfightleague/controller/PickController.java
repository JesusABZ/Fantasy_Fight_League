package com.fantasyfightleague.controller;

import com.fantasyfightleague.dto.CreatePickDTO;
import com.fantasyfightleague.dto.MessageResponseDTO;
import com.fantasyfightleague.dto.UpdatePickDTO;
import com.fantasyfightleague.model.*;
import com.fantasyfightleague.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/picks")
public class PickController {
    
    @Autowired
    private PickService pickService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LeagueService leagueService;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private FighterService fighterService;
    
    /**
     * Crear o actualizar pick de un usuario
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrUpdatePick(@RequestBody CreatePickDTO pickDTO) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            League league = leagueService.findById(pickDTO.getLeagueId())
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));
            
            Event event = eventService.findById(pickDTO.getEventId())
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
            
            // Verificar que el usuario es miembro de la liga
            if (!leagueService.isUserInLeague(league, user)) {
                return ResponseEntity.badRequest().body(new MessageResponseDTO("No eres miembro de esta liga"));
            }
            
            // Verificar que los picks están abiertos
            if (!event.isPicksOpen()) {
                return ResponseEntity.badRequest().body(new MessageResponseDTO("Los picks están cerrados para este evento"));
            }
            
            // Validar cantidad de luchadores (1-3)
            if (pickDTO.getFighterIds() == null || pickDTO.getFighterIds().isEmpty() || pickDTO.getFighterIds().size() > 3) {
                return ResponseEntity.badRequest().body(new MessageResponseDTO("Debes elegir entre 1 y 3 luchadores"));
            }
            
            // Crear o actualizar pick
            Pick pick = pickService.createOrUpdatePick(user, league, event, pickDTO.getFighterIds());
            
            return ResponseEntity.ok(pick);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener mi pick para una liga y evento específico
     */
    @GetMapping("/my-pick")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyPick(@RequestParam Long leagueId, @RequestParam Long eventId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            League league = leagueService.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));
            
            Event event = eventService.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
            
            Optional<Pick> pick = pickService.findByUserLeagueAndEvent(user, league, event);
            
            if (pick.isPresent()) {
                return ResponseEntity.ok(pick.get());
            } else {
                // Crear pick vacío para mostrar información inicial
                Pick emptyPick = new Pick(user, league, event);
                return ResponseEntity.ok(emptyPick);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener todos mis picks en una liga
     */
    @GetMapping("/my-picks/{leagueId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyPicksInLeague(@PathVariable Long leagueId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            League league = leagueService.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));
            
            List<Pick> picks = pickService.findByUserAndLeague(user, league);
            return ResponseEntity.ok(picks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener clasificación por evento en una liga
     */
    @GetMapping("/leaderboard/event")
    public ResponseEntity<?> getEventLeaderboard(@RequestParam Long leagueId, @RequestParam Long eventId) {
        try {
            League league = leagueService.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));
            
            Event event = eventService.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
            
            List<Pick> leaderboard = pickService.getEventLeaderboard(league, event);
            return ResponseEntity.ok(leaderboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener clasificación global en una liga
     */
    @GetMapping("/leaderboard/global/{leagueId}")
    public ResponseEntity<?> getGlobalLeaderboard(@PathVariable Long leagueId) {
        try {
            League league = leagueService.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));
            
            List<Map<String, Object>> leaderboard = pickService.getGlobalLeaderboard(league);
            return ResponseEntity.ok(leaderboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
    
    /**
     * Eliminar pick (solo si no está bloqueado)
     */
    @DeleteMapping("/{pickId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletePick(@PathVariable Long pickId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            Optional<Pick> pickOpt = pickService.findById(pickId);
            if (!pickOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Pick pick = pickOpt.get();
            
            // Verificar que el pick pertenece al usuario
            if (!pick.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body(new MessageResponseDTO("No puedes eliminar este pick"));
            }
            
            // Verificar que se puede modificar
            if (!pick.canBeModified()) {
                return ResponseEntity.badRequest().body(new MessageResponseDTO("No puedes modificar este pick (está bloqueado o pasó el deadline)"));
            }
            
            pickService.deletePick(pickId);
            return ResponseEntity.ok(new MessageResponseDTO("Pick eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
    
    /**
     * [ADMIN] Bloquear picks manualmente
     */
    @PostMapping("/admin/lock-picks/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> lockPicksForEvent(@PathVariable Long eventId) {
        try {
            Event event = eventService.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
            
            int lockedCount = pickService.lockPicksForEvent(event);
            return ResponseEntity.ok(new MessageResponseDTO("Se bloquearon " + lockedCount + " picks para el evento"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
}