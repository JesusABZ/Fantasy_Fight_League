package com.fantasyfightleague.service;

import com.fantasyfightleague.model.Event;
import com.fantasyfightleague.model.Fighter;
import com.fantasyfightleague.model.Fight;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SportradarService {

    private static final Logger logger = LoggerFactory.getLogger(SportradarService.class);
    private static final String API_BASE_URL = "https://api.sportradar.us/mma/trial/v2/en";
    
    @Value("${sportradar.api.key}")
    private String apiKey;
    
    private final RestTemplate restTemplate;
    private final FighterService fighterService;
    private final EventService eventService;
    
    @Autowired
    public SportradarService(RestTemplate restTemplate, FighterService fighterService, EventService eventService) {
        this.restTemplate = restTemplate;
        this.fighterService = fighterService;
        this.eventService = eventService;
    }
    
    /**
     * Obtiene el próximo evento de UFC programado
     */
    public Event getNextUFCEvent() {
        try {
            // La URL correcta es simplemente /schedules.json según la documentación
            String url = String.format("%s/schedules.json?api_key=%s", API_BASE_URL, apiKey);
            logger.info("Consultando programación de eventos: {}", url);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject jsonData = new JSONObject(response.getBody());
                
                // Para depuración, veamos toda la respuesta
                logger.debug("Respuesta completa: {}", response.getBody());
                
                if (jsonData.has("schedules")) {
                    JSONArray schedules = jsonData.getJSONArray("schedules");
                    logger.info("Se encontraron {} eventos programados", schedules.length());
                    
                    // Fecha actual para comparar
                    LocalDate currentDate = LocalDate.now();
                    
                    // Buscar el primer evento de UFC
                    for (int i = 0; i < schedules.length(); i++) {
                        JSONObject scheduleItem = schedules.getJSONObject(i);
                        JSONObject sportEvent = scheduleItem.getJSONObject("sport_event");
                        
                        // Verificar si es un evento de UFC
                        if (sportEvent.has("sport_event_context") && 
                            sportEvent.getJSONObject("sport_event_context").has("competition") &&
                            sportEvent.getJSONObject("sport_event_context").getJSONObject("competition").getString("name").contains("UFC")) {
                            
                            // Crear objeto Event
                            Event event = new Event();
                            event.setName(sportEvent.getString("name"));
                            
                            // Guardar el ID externo como descripción para futuras referencias
                            event.setDescription("SportradarID: " + sportEvent.getString("id"));
                            
                            // Convertir fecha ISO 8601 a Date
                            String scheduledStr = sportEvent.getString("scheduled");
                            LocalDate eventDate = LocalDate.parse(scheduledStr.substring(0, 10));
                            event.setDate(Date.from(eventDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                            
                            if (sportEvent.has("venue")) {
                                JSONObject venue = sportEvent.getJSONObject("venue");
                                event.setLocation(venue.getString("name") + ", " + venue.getString("city_name"));
                            }
                            
                            event.setStatus("SCHEDULED");
                            
                            // Solo devolver eventos futuros
                            if (eventDate.isAfter(currentDate) || eventDate.isEqual(currentDate)) {
                                logger.info("Próximo evento UFC encontrado: {}, fecha: {}", event.getName(), eventDate);
                                return event;
                            }
                        }
                    }
                }
                
                logger.warn("No se encontraron eventos próximos de UFC en la respuesta");
            } else {
                logger.warn("Error en la respuesta de la API: {}", response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("Error obteniendo próximo evento UFC: {}", e.getMessage(), e);
        }
        
        return null;
    } 
    /**
     * Obtiene los luchadores que participan en un evento específico
     */
    public List<Fighter> getEventFighters(String eventId) {
        List<Fighter> fighters = new ArrayList<>();
        Set<String> processedFighterIds = new HashSet<>();
        
        try {
            // Obtener detalles del evento
            String url = String.format("%s/sport_events/%s/summary.json?api_key=%s", 
                    API_BASE_URL, eventId, apiKey);
            logger.info("Consultando detalles del evento: {}", url);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject eventData = new JSONObject(response.getBody());
                
                if (eventData.has("fights")) {
                    JSONArray fights = eventData.getJSONArray("fights");
                    
                    // Procesar cada pelea
                    for (int i = 0; i < fights.length(); i++) {
                        JSONObject fight = fights.getJSONObject(i);
                        String weightClass = fight.optString("weight_class", "Unknown");
                        
                        // Procesar luchadores
                        if (fight.has("competitors")) {
                            JSONArray competitors = fight.getJSONArray("competitors");
                            
                            // Obtener detalles de cada luchador
                            for (int j = 0; j < competitors.length(); j++) {
                                JSONObject competitor = competitors.getJSONObject(j);
                                String fighterId = competitor.getString("id");
                                
                                // Evitar procesar el mismo luchador dos veces
                                if (!processedFighterIds.contains(fighterId)) {
                                    processedFighterIds.add(fighterId);
                                    
                                    // Obtener detalles completos del luchador
                                    Fighter fighter = getFighterDetails(fighterId, weightClass);
                                    if (fighter != null) {
                                        fighters.add(fighter);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            logger.info("Obtenidos {} luchadores para el evento", fighters.size());
            
        } catch (Exception e) {
            logger.error("Error obteniendo luchadores del evento: {}", e.getMessage(), e);
        }
        
        return fighters;
    }
    
    /**
     * Obtiene los detalles completos de un luchador
     */
    private Fighter getFighterDetails(String fighterId, String weightClass) {
        try {
            String url = String.format("%s/competitors/%s/profile.json?api_key=%s", 
                    API_BASE_URL, fighterId, apiKey);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject fighterData = new JSONObject(response.getBody());
                JSONObject competitor = fighterData.getJSONObject("competitor");
                
                Fighter fighter = new Fighter();
                fighter.setName(competitor.getString("name"));
                
                // Guardar ID externo en algún campo (para futuras referencias)
                // Por ejemplo, podemos usar el campo imageUrl temporalmente 
                // o crear un nuevo campo en el modelo Fighter
                fighter.setImageUrl("SportradarID: " + fighterId);
                
                // Establecer categoría de peso
                fighter.setWeightClass(mapWeightClass(weightClass));
                
                // Obtener nacionalidad si está disponible
                if (competitor.has("nationality")) {
                    fighter.setNationality(competitor.getString("nationality"));
                }
                
                // Obtener récord
                if (fighterData.has("statistics") && fighterData.getJSONObject("statistics").has("record")) {
                    JSONObject record = fighterData.getJSONObject("statistics").getJSONObject("record");
                    String recordStr = record.getInt("wins") + "-" + record.getInt("losses");
                    if (record.has("draws") && record.getInt("draws") > 0) {
                        recordStr += "-" + record.getInt("draws");
                    }
                    fighter.setRecord(recordStr);
                }
                
                // Datos físicos
                if (competitor.has("height_cm")) {
                    fighter.setHeight(competitor.getDouble("height_cm") / 100); // cm a metros
                }
                
                if (competitor.has("weight_kg")) {
                    fighter.setWeight(competitor.getDouble("weight_kg"));
                }
                
                // Calcular edad si hay fecha de nacimiento
                if (competitor.has("date_of_birth")) {
                    String dobStr = competitor.getString("date_of_birth");
                    LocalDate dob = LocalDate.parse(dobStr.substring(0, 10));
                    int age = LocalDate.now().getYear() - dob.getYear();
                    fighter.setAge(age);
                }
                
                // Calcular precio base
                int basePrice = calculateBasePrice(fighter);
                fighter.setBasePrice(basePrice);
                fighter.setPrice(basePrice);
                fighter.setActive(true); // Marcar como activo para el próximo evento
                
                return fighter;
            }
        } catch (Exception e) {
            logger.error("Error obteniendo detalles del luchador {}: {}", fighterId, e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Normaliza nombres de categorías de peso a formato estándar de UFC
     */
    private String mapWeightClass(String apiWeightClass) {
        Map<String, String> weightClassMap = new HashMap<>();
        weightClassMap.put("flyweight", "Flyweight");
        weightClassMap.put("bantamweight", "Bantamweight");
        weightClassMap.put("featherweight", "Featherweight");
        weightClassMap.put("lightweight", "Lightweight");
        weightClassMap.put("welterweight", "Welterweight");
        weightClassMap.put("middleweight", "Middleweight");
        weightClassMap.put("light heavyweight", "Light Heavyweight");
        weightClassMap.put("light_heavyweight", "Light Heavyweight");
        weightClassMap.put("heavyweight", "Heavyweight");
        weightClassMap.put("women's strawweight", "Women's Strawweight");
        weightClassMap.put("women's flyweight", "Women's Flyweight");
        weightClassMap.put("women's bantamweight", "Women's Bantamweight");
        weightClassMap.put("women's featherweight", "Women's Featherweight");
        
        String normalizedClass = apiWeightClass.toLowerCase().trim();
        return weightClassMap.getOrDefault(normalizedClass, apiWeightClass);
    }
    
    /**
     * Calcula precio base para un luchador
     */
    private int calculateBasePrice(Fighter fighter) {
        int basePrice = 50; // Precio base mínimo
        
        // Ajustar según categoría de peso
        String weightClass = fighter.getWeightClass() != null ? fighter.getWeightClass().toLowerCase() : "";
        if (weightClass.contains("heavyweight")) {
            basePrice += 20;
        } else if (weightClass.contains("middleweight") || weightClass.contains("welterweight")) {
            basePrice += 15;
        } else if (weightClass.contains("lightweight") || weightClass.contains("featherweight")) {
            basePrice += 10;
        } else {
            basePrice += 5;
        }
        
        // Ajustar según récord
        if (fighter.getRecord() != null) {
            String[] parts = fighter.getRecord().split("-");
            if (parts.length >= 2) {
                try {
                    int wins = Integer.parseInt(parts[0]);
                    int losses = Integer.parseInt(parts[1]);
                    
                    // Más victorias, mayor precio
                    basePrice += wins * 2;
                    
                    // Mejor ratio victoria/derrota, mayor precio
                    if (losses > 0) {
                        double ratio = (double) wins / losses;
                        if (ratio > 5) basePrice += 15;
                        else if (ratio > 3) basePrice += 10;
                        else if (ratio > 1) basePrice += 5;
                    } else if (wins > 0) {
                        // Invicto
                        basePrice += 20;
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Error analizando récord para luchador: {}", fighter.getName());
                }
            }
        }
        
        // Limitar el precio entre 50 y 100
        return Math.min(100, Math.max(50, basePrice));
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
     * Sincronización completa:
     * 1. Desactiva todos los luchadores activos
     * 2. Obtiene el próximo evento UFC
     * 3. Obtiene y activa los luchadores de ese evento
     */
    public void syncNextEventFighters() {
        try {
            logger.info("Iniciando sincronización de luchadores del próximo evento UFC");
            
            // 1. Desactivar todos los luchadores actuales
            int deactivatedCount = deactivateAllFighters();
            logger.info("Desactivados {} luchadores anteriores", deactivatedCount);
            
            // 2. Obtener el próximo evento UFC
            Event nextEvent = getNextUFCEvent();
            
            if (nextEvent != null) {
                // 3. Guardar el evento en la base de datos
                nextEvent = eventService.saveEvent(nextEvent);
                
                // 4. Obtener el ID externo del evento desde la descripción
                String externalEventId = nextEvent.getDescription().replace("SportradarID: ", "");
                
                // 5. Obtener luchadores del evento
                List<Fighter> eventFighters = getEventFighters(externalEventId);
                
                // 6. Procesar y guardar cada luchador
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
                        existingFighter.setPrice(newFighter.getPrice());
                        existingFighter.setBasePrice(newFighter.getBasePrice());
                        
                        fighterService.saveFighter(existingFighter);
                        updatedCount++;
                    } else {
                        // Añadir nuevo luchador
                        fighterService.saveFighter(newFighter);
                        addedCount++;
                    }
                }
                
                logger.info("Sincronización completada. Evento: {}, Luchadores - Añadidos: {}, Actualizados: {}", 
                        nextEvent.getName(), addedCount, updatedCount);
            } else {
                logger.warn("No se encontró ningún evento próximo de UFC");
            }
            
        } catch (Exception e) {
            logger.error("Error en la sincronización de luchadores: {}", e.getMessage(), e);
        }
    }
}