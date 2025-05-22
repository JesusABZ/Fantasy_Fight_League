// Crear: src/main/java/com/fantasyfightleague/controller/ScoringController.java
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
@RequestMapping("/api/scoring")
public class ScoringController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PickService pickService;
    
    @Autowired
    private FighterStatsRepository fighterStatsRepository;
    
    /**
     * Obtener puntuaciones detalladas de un pick específico
     */
    @GetMapping("/pick-details/{pickId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPickScoringDetails(@PathVariable Long pickId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            Optional<Pick> pickOpt = pickService.findById(pickId);
            if (!pickOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Pick pick = pickOpt.get();
            
            // Verificar que el pick pertenece al usuario actual
            if (!pick.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body("No tienes acceso a este pick");
            }
            
            // Crear respuesta con detalles de puntuación
            Map<String, Object> response = new HashMap<>();
            response.put("pick", pick);
            response.put("event", pick.getEvent());
            response.put("league", pick.getLeague());
            response.put("totalEventPoints", pick.getEventPoints());
            
            List<Map<String, Object>> fighterDetails = new ArrayList<>();
            
            for (Fighter fighter : pick.getSelectedFighters()) {
                Map<String, Object> fighterDetail = new HashMap<>();
                fighterDetail.put("fighter", fighter);
                
                // Buscar las estadísticas del luchador para este evento
                Optional<FighterStats> statsOpt = fighterStatsRepository
                    .findByFighter(fighter)
                    .stream()
                    .filter(stats -> stats.getCreatedAt().after(pick.getEvent().getStartDate()))
                    .findFirst();
                
                if (statsOpt.isPresent()) {
                    FighterStats stats = statsOpt.get();
                    fighterDetail.put("stats", stats);
                    fighterDetail.put("points", stats.getPoints());
                    
                    // Desglose de puntos
                    Map<String, Object> breakdown = createPointsBreakdown(stats);
                    fighterDetail.put("pointsBreakdown", breakdown);
                } else {
                    fighterDetail.put("stats", null);
                    fighterDetail.put("points", 0);
                    fighterDetail.put("pointsBreakdown", "No hay estadísticas disponibles");
                }
                
                fighterDetails.add(fighterDetail);
            }
            
            response.put("fighterDetails", fighterDetails);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Obtener resumen de puntuaciones de todos los picks de un usuario en una liga
     */
    @GetMapping("/user-league-summary/{leagueId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserLeagueSummary(@PathVariable Long leagueId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Obtener todos los picks del usuario en esta liga
            List<Pick> userPicks = pickService.findByUserAndLeague(user, 
                new League() {{ setId(leagueId); }});
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalPicks", userPicks.size());
            
            int totalPoints = userPicks.stream()
                    .mapToInt(Pick::getEventPoints)
                    .sum();
            response.put("totalPoints", totalPoints);
            
            List<Map<String, Object>> picksSummary = new ArrayList<>();
            for (Pick pick : userPicks) {
                Map<String, Object> pickSummary = new HashMap<>();
                pickSummary.put("pickId", pick.getId());
                pickSummary.put("eventName", pick.getEvent().getName());
                pickSummary.put("eventDate", pick.getEvent().getStartDate());
                pickSummary.put("points", pick.getEventPoints());
                pickSummary.put("fightersCount", pick.getSelectedFighters().size());
                
                List<String> fighterNames = new ArrayList<>();
                for (Fighter fighter : pick.getSelectedFighters()) {
                    fighterNames.add(fighter.getName());
                }
                pickSummary.put("fighterNames", fighterNames);
                
                picksSummary.add(pickSummary);
            }
            
            // Ordenar por puntos descendente
            picksSummary.sort((a, b) -> 
                Integer.compare((Integer) b.get("points"), (Integer) a.get("points")));
            
            response.put("picks", picksSummary);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Crear desglose detallado de puntos para un luchador
     */
    private Map<String, Object> createPointsBreakdown(FighterStats stats) {
        Map<String, Object> breakdown = new HashMap<>();
        
        // Calcular componentes de puntuación
        breakdown.put("totalPoints", stats.getPoints());
        
        List<Map<String, String>> components = new ArrayList<>();
        
        // Aquí reconstruimos el cálculo basado en las estadísticas guardadas
        // Nota: Esta es una aproximación ya que no guardamos el desglose exacto
        
        if (stats.getSignificantStrikes() != null && stats.getSignificantStrikes() > 0) {
            int strikePoints = (int) (stats.getSignificantStrikes() * 0.3);
            components.add(Map.of(
                "categoria", "Golpes Significantes",
                "cantidad", stats.getSignificantStrikes().toString(),
                "puntos", "+" + strikePoints
            ));
        }
        
        if (stats.getTakedowns() != null && stats.getTakedowns() > 0) {
            int takedownPoints = stats.getTakedowns() * 3;
            components.add(Map.of(
                "categoria", "Takedowns",
                "cantidad", stats.getTakedowns().toString(),
                "puntos", "+" + takedownPoints
            ));
        }
        
        if (stats.getKnockdowns() != null && stats.getKnockdowns() > 0) {
            int knockdownPoints = stats.getKnockdowns() * 8;
            components.add(Map.of(
                "categoria", "Knockdowns",
                "cantidad", stats.getKnockdowns().toString(),
                "puntos", "+" + knockdownPoints
            ));
        }
        
        if (stats.getSubmissions() != null && stats.getSubmissions() > 0) {
            int submissionPoints = stats.getSubmissions() * 2;
            components.add(Map.of(
                "categoria", "Intentos de Sumisión",
                "cantidad", stats.getSubmissions().toString(),
                "puntos", "+" + submissionPoints
            ));
        }
        
        breakdown.put("components", components);
        breakdown.put("message", "Puntuación basada en rendimiento en el combate");
        
        return breakdown;
    }
}