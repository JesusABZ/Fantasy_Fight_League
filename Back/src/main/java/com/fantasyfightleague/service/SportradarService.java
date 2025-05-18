package com.fantasyfightleague.service;

import com.fantasyfightleague.model.Event;
import com.fantasyfightleague.model.Fighter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SportradarService {

    private static final Logger logger = LoggerFactory.getLogger(SportradarService.class);
    
    private final FighterService fighterService;
    private final EventService eventService;
    private final SeleniumUFCScraperService seleniumUFCScraperService;
    
    @Autowired
    public SportradarService(FighterService fighterService, EventService eventService, SeleniumUFCScraperService seleniumUFCScraperService) {
        this.fighterService = fighterService;
        this.eventService = eventService;
        this.seleniumUFCScraperService = seleniumUFCScraperService;
    }
    
    /**
     * Desactiva todos los luchadores activos en la base de datos
     */
    public int deactivateAllFighters() {
        List<Fighter> activeFighters = fighterService.findAllActiveFighters();
        for (Fighter fighter : activeFighters) {
            fighter.setActive(false);
            fighterService.saveFighter(fighter);
        }
        return activeFighters.size();
    }
    
    /**
     * Sincronización completa desde la página de UFC
     */
    public void syncNextEventFighters() {
        syncFightersFromUFCWebsite("https://www.ufcespanol.com/event/ufc-fight-night-may-31-2025");
    }
    
    /**
     * Sincronización desde una URL específica de la UFC
     */
    public void syncFightersFromUFCWebsite(String eventUrl) {
        try {
            logger.info("Iniciando sincronización de luchadores desde {}", eventUrl);
            
            // 1. Desactivar todos los luchadores actuales
            int deactivatedCount = deactivateAllFighters();
            logger.info("Desactivados {} luchadores anteriores", deactivatedCount);
            
            // 2. Crear el evento
            Event nextEvent = createEventFromUFCWebsite(eventUrl);
            
            if (nextEvent != null) {
                // 3. Guardar el evento en la base de datos
                nextEvent = eventService.saveEvent(nextEvent);
                
                // 4. Obtener luchadores del evento
                List<Fighter> eventFighters = extractFightersFromUFC(eventUrl);
                
                // 5. Procesar y guardar cada luchador
                int addedCount = 0;
                int updatedCount = 0;
                
                for (Fighter newFighter : eventFighters) {
                    // Buscar si ya existe el luchador
                    List<Fighter> existingFighters = fighterService.findByName(newFighter.getName());
                    
                    if (!existingFighters.isEmpty()) {
                        // Actualizar luchador existente
                        Fighter existingFighter = existingFighters.get(0);
                        
                        // Actualizar los campos necesarios
                        existingFighter.setWeightClass(newFighter.getWeightClass());
                        existingFighter.setRecord(newFighter.getRecord());
                        existingFighter.setNationality(newFighter.getNationality());
                        existingFighter.setAge(newFighter.getAge());
                        existingFighter.setHeight(newFighter.getHeight());
                        existingFighter.setWeight(newFighter.getWeight());
                        existingFighter.setActive(true); // Marcar como activo
                        
                        fighterService.saveFighter(existingFighter);
                        updatedCount++;
                    } else {
                        // Añadir nuevo luchador
                        fighterService.saveFighter(newFighter);
                        addedCount++;
                    }
                }
                
                logger.info("Sincronización desde UFC website completada. Evento: {}, Luchadores - Añadidos: {}, Actualizados: {}", 
                        nextEvent.getName(), addedCount, updatedCount);
            } else {
                logger.warn("No se pudo crear el evento desde el sitio de UFC");
            }
            
        } catch (Exception e) {
            logger.error("Error en la sincronización de luchadores desde UFC website: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Extrae información del evento desde el sitio de UFC
     */
    private Event createEventFromUFCWebsite(String eventUrl) {
        try {
            String html = fetchWebContent(eventUrl);
            
            // Extraer nombre del evento
            Pattern eventNamePattern = Pattern.compile("<title>(.*?)</title>");
            Matcher eventNameMatcher = eventNamePattern.matcher(html);
            
            String eventName = "UFC Event";
            if (eventNameMatcher.find()) {
                eventName = eventNameMatcher.group(1).trim();
                // Limpiar el nombre si es necesario
                if (eventName.contains("|")) {
                    eventName = eventName.substring(0, eventName.indexOf("|")).trim();
                }
            }
            
            // Extraer fecha del evento
            Pattern datePattern = Pattern.compile("data-event-date=\"(.*?)\"");
            Matcher dateMatcher = datePattern.matcher(html);
            
            Date eventDate = new Date();
            if (dateMatcher.find()) {
                String dateStr = dateMatcher.group(1).trim();
                try {
                    // Convertir formato de fecha (pueden ser varios formatos)
                    if (dateStr.contains("-")) {
                        LocalDate localDate = LocalDate.parse(dateStr.substring(0, 10));
                        eventDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    }
                } catch (Exception e) {
                    logger.warn("Error al parsear la fecha del evento: {}", e.getMessage());
                }
            } else {
                // Si no encontramos la fecha, intentamos extraerla de la URL
                Pattern urlDatePattern = Pattern.compile("ufc-fight-night-([a-z]+-\\d+-\\d+)");
                Matcher urlDateMatcher = urlDatePattern.matcher(eventUrl);
                
                if (urlDateMatcher.find()) {
                    String dateUrlPart = urlDateMatcher.group(1);
                    try {
                        String[] dateParts = dateUrlPart.split("-");
                        if (dateParts.length >= 3) {
                            int month = getMonthNumber(dateParts[0]);
                            int day = Integer.parseInt(dateParts[1]);
                            int year = Integer.parseInt(dateParts[2]);
                            
                            Calendar cal = Calendar.getInstance();
                            cal.set(year, month - 1, day);
                            eventDate = cal.getTime();
                        }
                    } catch (Exception e) {
                        logger.warn("Error al parsear la fecha de la URL: {}", e.getMessage());
                    }
                }
            }
            
            // Extraer ubicación del evento
            Pattern locationPattern = Pattern.compile("<div class=\"event-info-location\">(.*?)</div>");
            Matcher locationMatcher = locationPattern.matcher(html);
            
            String location = "UFC Apex, Las Vegas, Nevada";
            if (locationMatcher.find()) {
                location = locationMatcher.group(1).trim()
                    .replaceAll("<[^>]*>", "") // Eliminar etiquetas HTML
                    .replaceAll("\\s+", " "); // Limpiar espacios extras
            }
            
            // Crear el objeto Event
            Event event = new Event();
            event.setName(eventName);
            event.setDescription("Evento extraído de: " + eventUrl);
            event.setDate(eventDate);
            event.setLocation(location);
            event.setStatus("SCHEDULED");
            
            return event;
            
        } catch (Exception e) {
            logger.error("Error creando evento desde UFC website: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Helper para convertir nombre de mes a número
     */
    private int getMonthNumber(String monthName) {
        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("january", 1);
        monthMap.put("february", 2);
        monthMap.put("march", 3);
        monthMap.put("april", 4);
        monthMap.put("may", 5);
        monthMap.put("june", 6);
        monthMap.put("july", 7);
        monthMap.put("august", 8);
        monthMap.put("september", 9);
        monthMap.put("october", 10);
        monthMap.put("november", 11);
        monthMap.put("december", 12);
        
        return monthMap.getOrDefault(monthName.toLowerCase(), 1);
    }
    
    /**
     * Extrae luchadores desde el sitio web de UFC usando Selenium
     */
    private List<Fighter> extractFightersFromUFC(String eventUrl) {
        try {
            return seleniumUFCScraperService.extractFightersFromUFC(eventUrl);
        } catch (Exception e) {
            logger.error("Error al extraer peleadores con Selenium: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene el contenido HTML de una URL
     */
    private String fetchWebContent(String urlString) throws Exception {
        StringBuilder content = new StringBuilder();
        
        java.net.URL url = new java.net.URL(urlString);
        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        return content.toString();
    }
}