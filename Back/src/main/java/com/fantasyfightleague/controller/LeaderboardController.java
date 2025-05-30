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
     * MEJORADO: Incluye toda la información necesaria para la UI
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
            
            // 🔥 MEJORADO: Obtener clasificación global con datos completos
            List<Map<String, Object>> leaderboard = getEnhancedGlobalLeaderboard(league);
            
            Map<String, Object> response = new HashMap<>();
            response.put("league", league);
            response.put("leaderboard", leaderboard);
            response.put("totalParticipants", leaderboard.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * 🆕 NUEVO MÉTODO: Obtener clasificación global enriquecida con todos los datos necesarios
     */
    private List<Map<String, Object>> getEnhancedGlobalLeaderboard(League league) {
        // Obtener la clasificación base
        List<Map<String, Object>> baseLeaderboard = pickService.getGlobalLeaderboard(league);
        
        // Obtener todos los miembros de la liga (incluso los que no han participado)
        Set<User> allMembers = league.getMembers();
        Map<Long, Map<String, Object>> leaderboardMap = new HashMap<>();
        
        // Procesar miembros que ya están en la clasificación
        for (Map<String, Object> entry : baseLeaderboard) {
            User entryUser = (User) entry.get("user");
            Long totalPoints = (Long) entry.get("totalPoints");
            
            Map<String, Object> enrichedEntry = createEnrichedEntry(entryUser, league, totalPoints != null ? totalPoints.intValue() : 0);
            leaderboardMap.put(entryUser.getId(), enrichedEntry);
        }
        
        // Agregar miembros que no han participado aún
        for (User member : allMembers) {
            if (!leaderboardMap.containsKey(member.getId())) {
                Map<String, Object> enrichedEntry = createEnrichedEntry(member, league, 0);
                leaderboardMap.put(member.getId(), enrichedEntry);
            }
        }
        
        // Convertir a lista y ordenar por puntos (descendente)
        List<Map<String, Object>> finalLeaderboard = new ArrayList<>(leaderboardMap.values());
        finalLeaderboard.sort((a, b) -> {
            Integer pointsA = (Integer) a.get("totalPoints");
            Integer pointsB = (Integer) b.get("totalPoints");
            return pointsB.compareTo(pointsA);
        });
        
        // Asignar posiciones
        for (int i = 0; i < finalLeaderboard.size(); i++) {
            finalLeaderboard.get(i).put("position", i + 1);
        }
        
        return finalLeaderboard;
    }
    
    /**
     * 🆕 NUEVO MÉTODO: Crear entrada enriquecida con todos los datos necesarios
     */
    private Map<String, Object> createEnrichedEntry(User user, League league, int totalPoints) {
        Map<String, Object> entry = new HashMap<>();
        
        // 📊 Información básica del usuario
        entry.put("userId", user.getId());
        entry.put("username", user.getUsername());
        entry.put("firstName", user.getFirstName() != null ? user.getFirstName() : "");
        entry.put("lastName", user.getLastName() != null ? user.getLastName() : "");
        entry.put("profileImageUrl", user.getProfileImageUrl());
        
        // 🏆 Estadísticas de puntos
        entry.put("totalPoints", totalPoints);
        
        // 📈 Obtener información de participación
        List<Pick> userPicks = pickService.findByUserAndLeague(user, league);
        entry.put("eventsParticipated", userPicks.size());
        
        // 🎯 Calcular promedio de puntos por evento
        if (userPicks.size() > 0) {
            double avgPoints = (double) totalPoints / userPicks.size();
            entry.put("averagePointsPerEvent", Math.round(avgPoints * 100.0) / 100.0);
        } else {
            entry.put("averagePointsPerEvent", 0.0);
        }
        
        // ⚡ Puntos del último evento
        int lastEventPoints = getLastEventPoints(user, league, userPicks);
        entry.put("lastEventPoints", lastEventPoints);
        
        // 🎮 Información adicional
        entry.put("isCurrentUser", false); // Se establecerá en el frontend
        
        return entry;
    }
    
    /**
     * 🆕 NUEVO MÉTODO: Obtener puntos del último evento para un usuario
     */
    private int getLastEventPoints(User user, League league, List<Pick> userPicks) {
        if (userPicks.isEmpty()) {
            return 0;
        }
        
        // Ordenar picks por fecha del evento (más reciente primero)
        userPicks.sort((a, b) -> {
            Date dateA = a.getEvent().getStartDate() != null ? a.getEvent().getStartDate() : a.getEvent().getDate();
            Date dateB = b.getEvent().getStartDate() != null ? b.getEvent().getStartDate() : b.getEvent().getDate();
            return dateB.compareTo(dateA);
        });
        
        // Devolver los puntos del evento más reciente
        return userPicks.get(0).getEventPoints();
    }
    
    /**
     * Obtener clasificación de un evento específico en una liga
     * MEJORADO para incluir información completa del usuario
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
                entry.put("firstName", pickUser.getFirstName() != null ? pickUser.getFirstName() : "");
                entry.put("lastName", pickUser.getLastName() != null ? pickUser.getLastName() : "");
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
                        .filter(stats -> {
                            Date eventStart = event.getStartDate() != null ? event.getStartDate() : event.getDate();
                            return stats.getCreatedAt().after(eventStart);
                        })
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
                entry.put("fightersSelected", pick.getSelectedFighters().size());
                
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
            
            List<Map<String, Object>> leaderboard = getEnhancedGlobalLeaderboard(league);
            
            // Buscar la posición del usuario
            Map<String, Object> userPosition = null;
            
            for (Map<String, Object> entry : leaderboard) {
                Long userId = (Long) entry.get("userId");
                if (userId.equals(user.getId())) {
                    userPosition = new HashMap<>();
                    userPosition.put("position", entry.get("position"));
                    userPosition.put("totalPoints", entry.get("totalPoints"));
                    userPosition.put("totalParticipants", leaderboard.size());
                    userPosition.put("eventsParticipated", entry.get("eventsParticipated"));
                    userPosition.put("averagePointsPerEvent", entry.get("averagePointsPerEvent"));
                    userPosition.put("lastEventPoints", entry.get("lastEventPoints"));
                    break;
                }
            }
            
            if (userPosition == null) {
                userPosition = new HashMap<>();
                userPosition.put("position", null);
                userPosition.put("totalPoints", 0);
                userPosition.put("totalParticipants", leaderboard.size());
                userPosition.put("eventsParticipated", 0);
                userPosition.put("averagePointsPerEvent", 0.0);
                userPosition.put("lastEventPoints", 0);
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
            userPicks.sort((a, b) -> {
                Date dateA = a.getEvent().getStartDate() != null ? a.getEvent().getStartDate() : a.getEvent().getDate();
                Date dateB = b.getEvent().getStartDate() != null ? b.getEvent().getStartDate() : b.getEvent().getDate();
                return dateB.compareTo(dateA);
            });
            
            List<Map<String, Object>> history = new ArrayList<>();
            int totalPoints = 0;
            
            for (Pick pick : userPicks) {
                Map<String, Object> pickInfo = new HashMap<>();
                pickInfo.put("pickId", pick.getId());
                pickInfo.put("eventId", pick.getEvent().getId());
                pickInfo.put("eventName", pick.getEvent().getName());
                
                Date eventDate = pick.getEvent().getStartDate() != null ? 
                    pick.getEvent().getStartDate() : pick.getEvent().getDate();
                pickInfo.put("eventDate", eventDate);
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
}