// Back/src/main/java/com/fantasyfightleague/controller/LeaderboardController.java
package com.fantasyfightleague.controller;

import com.fantasyfightleague.model.*;
import com.fantasyfightleague.repository.FighterStatsRepository;
import com.fantasyfightleague.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LeagueService leagueService;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private PickService pickService;
    
    @Autowired
    private FighterStatsRepository fighterStatsRepository;
    
    /**
     * Obtener clasificación global de una liga (suma de todos los eventos)
     */
    @GetMapping("/global/{leagueId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getGlobalLeaderboard(@PathVariable Long leagueId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            League league = leagueService.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));
            
            // Verificar que el usuario es miembro de la liga
            if (!leagueService.isUserInLeague(league, user)) {
                return ResponseEntity.badRequest().body("No eres miembro de esta liga");
            }
            
            List<Map<String, Object>> leaderboard = pickService.getGlobalLeaderboard(league);
            
            // 🔥 MEJORADO: Enriquecer los datos con información completa del usuario
            List<Map<String, Object>> enrichedLeaderboard = new ArrayList<>();
            int position = 1;
            
            for (Map<String, Object> entry : leaderboard) {
                Map<String, Object> enrichedEntry = new HashMap<>();
                User entryUser = (User) entry.get("user");
                Long totalPoints = (Long) entry.get("totalPoints");
                
                // Información básica del usuario
                enrichedEntry.put("position", position);
                enrichedEntry.put("userId", entryUser.getId());
                enrichedEntry.put("username", entryUser.getUsername());
                
                // 🆕 AGREGADO: Información completa del perfil
                enrichedEntry.put("firstName", entryUser.getFirstName());
                enrichedEntry.put("lastName", entryUser.getLastName());
                enrichedEntry.put("profileImageUrl", entryUser.getProfileImageUrl());
                
                // Estadísticas de puntos
                enrichedEntry.put("totalPoints", totalPoints != null ? totalPoints : 0);
                
                // Contar número de eventos participados
                List<Pick> userPicks = pickService.findByUserAndLeague(entryUser, league);
                enrichedEntry.put("eventsParticipated", userPicks.size());
                
                // Calcular promedio de puntos por evento
                if (!userPicks.isEmpty()) {
                    double avgPoints = totalPoints != null ? (double) totalPoints / userPicks.size() : 0;
                    enrichedEntry.put("averagePointsPerEvent", Math.round(avgPoints * 100.0) / 100.0);
                } else {
                    enrichedEntry.put("averagePointsPerEvent", 0.0);
                }
                
                // 🆕 AGREGADO: Puntos del último evento (por ahora 0, TODO: implementar)
                enrichedEntry.put("lastEventPoints", getLastEventPoints(entryUser, league));
                
                enrichedLeaderboard.add(enrichedEntry);
                position++;
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("league", league);
            response.put("leaderboard", enrichedLeaderboard);
            response.put("totalParticipants", enrichedLeaderboard.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Obtener clasificación de un evento específico en una liga
     */
    @GetMapping("/event/{leagueId}/{eventId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getEventLeaderboard(@PathVariable Long leagueId, @PathVariable Long eventId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            League league = leagueService.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));
            
            Event event = eventService.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
            
            // Verificar que el usuario es miembro de la liga
            if (!leagueService.isUserInLeague(league, user)) {
                return ResponseEntity.badRequest().body("No eres miembro de esta liga");
            }
            
            List<Pick> eventPicks = pickService.getEventLeaderboard(league, event);
            
            // 🔥 MEJORADO: Crear respuesta detallada con información completa del usuario
            List<Map<String, Object>> leaderboard = new ArrayList<>();
            int position = 1;
            
            for (Pick pick : eventPicks) {
                Map<String, Object> entry = new HashMap<>();
                User pickUser = pick.getUser();
                
                // Información de posición
                entry.put("position", position);
                
                // Información básica del usuario
                entry.put("userId", pickUser.getId());
                entry.put("username", pickUser.getUsername());
                
                // 🆕 AGREGADO: Información completa del perfil
                entry.put("firstName", pickUser.getFirstName());
                entry.put("lastName", pickUser.getLastName());
                entry.put("profileImageUrl", pickUser.getProfileImageUrl());
                
                // Estadísticas del evento
                entry.put("eventPoints", pick.getEventPoints());
                entry.put("totalCost", pick.getTotalCost());
                entry.put("remainingBudget", pick.getRemainingBudget());
                
                // Detalles de los luchadores seleccionados
                List<Map<String, Object>> fighterDetails = new ArrayList<>();
                for (Fighter fighter : pick.getSelectedFighters()) {
                    Map<String, Object> fighterInfo = new HashMap<>();
                    fighterInfo.put("fighterId", fighter.getId());
                    fighterInfo.put("name", fighter.getName());
                    fighterInfo.put("imageUrl", fighter.getImageUrl());
                    fighterInfo.put("price", fighter.getPrice());
                    
                    // Buscar puntos obtenidos por este luchador en este evento
                    Optional<FighterStats> statsOpt = fighterStatsRepository
                        .findByFighter(fighter)
                        .stream()
                        .filter(stats -> stats.getCreatedAt().after(event.getStartDate()))
                        .findFirst();
                    
                    if (statsOpt.isPresent()) {
                        fighterInfo.put("pointsEarned", statsOpt.get().getPoints());
                        fighterInfo.put("hasStats", true);
                    } else {
                        fighterInfo.put("pointsEarned", 0);
                        fighterInfo.put("hasStats", false);
                    }
                    
                    fighterDetails.add(fighterInfo);
                }
                
                entry.put("selectedFighters", fighterDetails);
                entry.put("fightersSelected", pick.getSelectedFighters().size()); // Para compatibilidad
                
                leaderboard.add(entry);
                position++;
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("league", league);
            response.put("event", event);
            response.put("leaderboard", leaderboard);
            response.put("totalParticipants", leaderboard.size());
            response.put("isEventCompleted", "COMPLETED".equals(event.getStatus()));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Obtener posición específica de un usuario en la clasificación global de una liga
     */
    @GetMapping("/my-position/{leagueId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyPosition(@PathVariable Long leagueId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            League league = leagueService.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));
            
            // Verificar que el usuario es miembro de la liga
            if (!leagueService.isUserInLeague(league, user)) {
                return ResponseEntity.badRequest().body("No eres miembro de esta liga");
            }
            
            List<Map<String, Object>> leaderboard = pickService.getGlobalLeaderboard(league);
            
            // Buscar la posición del usuario
            int position = 1;
            Map<String, Object> userPosition = null;
            
            for (Map<String, Object> entry : leaderboard) {
                User entryUser = (User) entry.get("user");
                if (entryUser.getId().equals(user.getId())) {
                    userPosition = new HashMap<>();
                    userPosition.put("position", position);
                    userPosition.put("totalPoints", entry.get("totalPoints"));
                    userPosition.put("totalParticipants", leaderboard.size());
                    
                    // Calcular eventos participados
                    List<Pick> userPicks = pickService.findByUserAndLeague(user, league);
                    userPosition.put("eventsParticipated", userPicks.size());
                    
                    break;
                }
                position++;
            }
            
            if (userPosition == null) {
                userPosition = new HashMap<>();
                userPosition.put("position", null);
                userPosition.put("totalPoints", 0);
                userPosition.put("totalParticipants", leaderboard.size());
                userPosition.put("eventsParticipated", 0);
                userPosition.put("message", "No has participado en ningún evento de esta liga");
            }
            
            return ResponseEntity.ok(userPosition);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Obtener historial completo de picks de un usuario en una liga
     */
    @GetMapping("/my-history/{leagueId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyHistory(@PathVariable Long leagueId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            League league = leagueService.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));
            
            // Verificar que el usuario es miembro de la liga
            if (!leagueService.isUserInLeague(league, user)) {
                return ResponseEntity.badRequest().body("No eres miembro de esta liga");
            }
            
            List<Pick> userPicks = pickService.findByUserAndLeague(user, league);
            
            // Ordenar por fecha del evento (más reciente primero)
            userPicks.sort((a, b) -> b.getEvent().getStartDate().compareTo(a.getEvent().getStartDate()));
            
            List<Map<String, Object>> history = new ArrayList<>();
            int totalPoints = 0;
            
            for (Pick pick : userPicks) {
                Map<String, Object> pickInfo = new HashMap<>();
                pickInfo.put("pickId", pick.getId());
                pickInfo.put("eventId", pick.getEvent().getId());
                pickInfo.put("eventName", pick.getEvent().getName());
                pickInfo.put("eventDate", pick.getEvent().getStartDate());
                pickInfo.put("eventStatus", pick.getEvent().getStatus());
                pickInfo.put("eventPoints", pick.getEventPoints());
                pickInfo.put("totalCost", pick.getTotalCost());
                pickInfo.put("isLocked", pick.isLocked());
                
                totalPoints += pick.getEventPoints();
                
                // Lista de luchadores seleccionados
                List<String> fighterNames = new ArrayList<>();
                for (Fighter fighter : pick.getSelectedFighters()) {
                    fighterNames.add(fighter.getName());
                }
                pickInfo.put("fighterNames", fighterNames);
                pickInfo.put("fightersCount", pick.getSelectedFighters().size());
                
                history.add(pickInfo);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("league", league);
            response.put("totalEvents", userPicks.size());
            response.put("totalPoints", totalPoints);
            response.put("history", history);
            
            if (!userPicks.isEmpty()) {
                response.put("averagePointsPerEvent", Math.round((double) totalPoints / userPicks.size() * 100.0) / 100.0);
            } else {
                response.put("averagePointsPerEvent", 0.0);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * 🆕 MÉTODO HELPER: Obtener puntos del último evento para un usuario
     */
    private int getLastEventPoints(User user, League league) {
        try {
            // Obtener todos los picks del usuario en esta liga
            List<Pick> userPicks = pickService.findByUserAndLeague(user, league);
            
            if (userPicks.isEmpty()) {
                return 0;
            }
            
            // Ordenar por fecha del evento (más reciente primero)
            userPicks.sort((a, b) -> b.getEvent().getStartDate().compareTo(a.getEvent().getStartDate()));
            
            // Devolver los puntos del evento más reciente
            return userPicks.get(0).getEventPoints();
            
        } catch (Exception e) {
            // En caso de error, devolver 0
            return 0;
        }
    }
}