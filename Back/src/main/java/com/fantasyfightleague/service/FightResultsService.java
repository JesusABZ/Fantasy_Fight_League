// Crear: src/main/java/com/fantasyfightleague/service/FightResultsService.java
package com.fantasyfightleague.service;

import com.fantasyfightleague.dto.EventResultsDTO;
import com.fantasyfightleague.dto.FightResultDTO;
import com.fantasyfightleague.model.Event;
import com.fantasyfightleague.model.Fighter;
import com.fantasyfightleague.model.FighterStats;
import com.fantasyfightleague.model.Pick;
import com.fantasyfightleague.repository.FighterStatsRepository;
import com.fantasyfightleague.repository.PickRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para procesar y guardar resultados de eventos UFC
 */
@Service
public class FightResultsService {
    
    private static final Logger logger = LoggerFactory.getLogger(FightResultsService.class);
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private FighterService fighterService;
    
    @Autowired
    private ScoringService scoringService;
    
    @Autowired
    private FighterStatsRepository fighterStatsRepository;
    
    @Autowired
    private PickRepository pickRepository;
    
    /**
     * Procesa los resultados de un evento completo
     * @param eventResults Resultados del evento
     * @return Número de luchadores procesados
     */
    @Transactional
    public int processEventResults(EventResultsDTO eventResults) {
        logger.info("Procesando resultados del evento: {}", eventResults.getNombreEvento());
        
        // 1. Buscar el evento por nombre
        Optional<Event> eventOpt = findEventByName(eventResults.getNombreEvento());
        if (!eventOpt.isPresent()) {
            throw new RuntimeException("No se encontró el evento: " + eventResults.getNombreEvento());
        }
        
        Event event = eventOpt.get();
        
        // 2. Procesar cada resultado individual
        int processedFighters = 0;
        StringBuilder processingLog = new StringBuilder();
        processingLog.append("Procesando evento: ").append(event.getName()).append("\n");
        
        for (FightResultDTO result : eventResults.getResultados()) {
            try {
                processingLog.append(processFighterResult(event, result)).append("\n");
                processedFighters++;
            } catch (Exception e) {
                logger.error("Error procesando resultado para {}: {}", result.getNombre(), e.getMessage());
                processingLog.append("ERROR - ").append(result.getNombre()).append(": ").append(e.getMessage()).append("\n");
            }
        }
        
        // 3. Actualizar puntuaciones de picks después de procesar todos los resultados
        updatePickScores(event);
        
        // 4. Marcar evento como completado
        event.setStatus("COMPLETED");
        eventService.saveEvent(event);
        
        logger.info("Resultados procesados exitosamente. Luchadores procesados: {}", processedFighters);
        logger.info("Log detallado:\n{}", processingLog.toString());
        
        return processedFighters;
    }
    
    /**
     * Procesa el resultado de un luchador individual
     */
    private String processFighterResult(Event event, FightResultDTO result) {
        // 1. Buscar el luchador por nombre
        List<Fighter> fighters = fighterService.findByName(result.getNombre());
        if (fighters.isEmpty()) {
            throw new RuntimeException("No se encontró el luchador: " + result.getNombre());
        }
        
        Fighter fighter = fighters.get(0); // Tomar el primero si hay varios
        
        // 2. Verificar que el luchador participa en este evento
        if (!event.getFighters().contains(fighter)) {
            throw new RuntimeException("El luchador " + result.getNombre() + " no participa en este evento");
        }
        
        // 3. Calcular puntos fantasy
        int fantasyPoints = scoringService.calculateFantasyPoints(result);
        
        // 4. Crear y guardar las estadísticas del luchador
        FighterStats stats = new FighterStats();
        stats.setFighter(fighter);
        stats.setFight(null); // No tenemos la pelea específica, solo el evento
        
        // Mapear las estadísticas
        stats.setSignificantStrikes(result.getGolpesSignificantes());
        stats.setTotalStrikes(result.getGolpesTotales());
        stats.setTakedowns(result.getTakedownsAcertados());
        stats.setSubmissions(result.getIntentosDeSumision());
        stats.setKnockdowns(result.getKnockdowns());
        stats.setPoints(fantasyPoints);
        
        // Calcular minutos luchados basado en el round
        if (result.getRound() != null) {
            int minutesFought = (result.getRound() - 1) * 5; // 5 minutos por round completo
            if ("Win".equalsIgnoreCase(result.getResultado()) && 
                !result.getMetodo().toLowerCase().contains("decision")) {
                // Si ganó por finish, agregar tiempo parcial del último round (estimado)
                minutesFought += 3; // Estimación promedio
            } else if (result.getRound() >= 3) {
                // Si fue a decisión, agregar el tiempo completo del último round
                minutesFought += 5;
            }
            stats.setMinutesFought(minutesFought);
        }
        
        fighterStatsRepository.save(stats);
        
        // 5. Generar log detallado
        String breakdown = scoringService.generatePointsBreakdown(result, fantasyPoints);
        
        return String.format("✅ %s - %d puntos\n%s", 
                            fighter.getName(), fantasyPoints, breakdown);
    }
    
    /**
     * Actualiza las puntuaciones de todos los picks del evento
     */
    private void updatePickScores(Event event) {
        logger.info("Actualizando puntuaciones de picks para el evento: {}", event.getName());
        
        // Obtener TODOS los picks de TODAS las ligas para este evento
        List<Pick> eventPicks = pickRepository.findByLeagueAndEvent(null, event);
        
        // Si no encuentra picks con liga null, buscar todos los picks del evento
        if (eventPicks.isEmpty()) {
            eventPicks = pickRepository.findAll().stream()
                .filter(pick -> pick.getEvent().getId().equals(event.getId()))
                .collect(java.util.stream.Collectors.toList());
        }
        
        logger.info("Encontrados {} picks para actualizar en el evento {}", eventPicks.size(), event.getName());
        
        for (Pick pick : eventPicks) {
            int totalEventPoints = 0;
            
            // Calcular puntos sumando las estadísticas de cada luchador seleccionado
            for (Fighter selectedFighter : pick.getSelectedFighters()) {
                // Buscar las estadísticas más recientes de este luchador para este evento
                Optional<FighterStats> statsOpt = fighterStatsRepository
                    .findByFighter(selectedFighter)
                    .stream()
                    .filter(stats -> stats.getCreatedAt().after(event.getStartDate()))
                    .findFirst();
                
                if (statsOpt.isPresent()) {
                    totalEventPoints += statsOpt.get().getPoints();
                    logger.debug("Luchador {} contribuye con {} puntos al pick del usuario {} en liga {}", 
                               selectedFighter.getName(), 
                               statsOpt.get().getPoints(), 
                               pick.getUser().getUsername(),
                               pick.getLeague().getName());
                }
            }
            
            // Actualizar los puntos del pick
            pick.setEventPoints(totalEventPoints);
            pickRepository.save(pick);
            
            logger.info("Pick actualizado - Usuario: {}, Liga: {}, Puntos: {}", 
                       pick.getUser().getUsername(), 
                       pick.getLeague().getName(),
                       totalEventPoints);
        }
    }
    
    /**
     * Busca un evento por nombre (búsqueda flexible)
     */
    private Optional<Event> findEventByName(String eventName) {
        List<Event> events = eventService.findByName(eventName);
        
        if (!events.isEmpty()) {
            return Optional.of(events.get(0));
        }
        
        // Si no encuentra por nombre exacto, buscar por nombre parcial
        List<Event> allEvents = eventService.findAllEvents();
        return allEvents.stream()
                .filter(event -> event.getName().toLowerCase().contains(eventName.toLowerCase()) ||
                               eventName.toLowerCase().contains(event.getName().toLowerCase()))
                .findFirst();
    }
    
    /**
     * Obtiene un resumen de puntuaciones para un evento
     */
    public String getEventScoringSummary(Long eventId) {
        Optional<Event> eventOpt = eventService.findById(eventId);
        if (!eventOpt.isPresent()) {
            return "Evento no encontrado";
        }
        
        Event event = eventOpt.get();
        List<Pick> eventPicks = pickRepository.findByLeagueAndEventOrderByEventPointsDesc(null, event);
        
        StringBuilder summary = new StringBuilder();
        summary.append("Resumen de puntuaciones - ").append(event.getName()).append("\n");
        summary.append("==========================================\n");
        
        for (int i = 0; i < Math.min(10, eventPicks.size()); i++) {
            Pick pick = eventPicks.get(i);
            summary.append(String.format("%d. %s - %d puntos\n", 
                         i + 1, pick.getUser().getUsername(), pick.getEventPoints()));
        }
        
        return summary.toString();
    }
}