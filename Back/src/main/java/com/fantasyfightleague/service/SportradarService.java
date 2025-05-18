package com.fantasyfightleague.service;

import com.fantasyfightleague.model.Event;
import com.fantasyfightleague.model.Fighter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    
    @Autowired
    public SportradarService(FighterService fighterService, EventService eventService) {
        this.fighterService = fighterService;
        this.eventService = eventService;
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
     * Extrae luchadores desde el sitio web de UFC
     */
    private List<Fighter> extractFightersFromUFC(String eventUrl) {
        List<Fighter> fighters = new ArrayList<>();
        
        try {
            String html = fetchWebContent(eventUrl);
            
            // Extraer bloques de cada pelea
            Pattern fightBlockPattern = Pattern.compile("<div class=\"c-listing-fight\">(.*?)</div>\\s*</div>\\s*</div>", Pattern.DOTALL);
            Matcher fightBlockMatcher = fightBlockPattern.matcher(html);
            
            while (fightBlockMatcher.find()) {
                String fightBlockHtml = fightBlockMatcher.group(1);
                
                // Extraer categoría de peso
                String weightClass = "Unknown";
                Pattern weightClassPattern = Pattern.compile("<div class=\"c-listing-fight__class\">(.*?)</div>");
                Matcher weightClassMatcher = weightClassPattern.matcher(fightBlockHtml);
                if (weightClassMatcher.find()) {
                    weightClass = weightClassMatcher.group(1).trim();
                }
                
                // Extraer información de los luchadores
                Pattern fighterPattern = Pattern.compile("<div class=\"c-listing-fight__corner-name\">(.*?)</div>");
                Matcher fighterMatcher = fighterPattern.matcher(fightBlockHtml);
                
                while (fighterMatcher.find()) {
                    String fighterName = fighterMatcher.group(1).trim()
                        .replaceAll("<[^>]*>", "") // Eliminar etiquetas HTML
                        .trim();
                    
                    // Crear luchador
                    Fighter fighter = new Fighter();
                    fighter.setName(fighterName);
                    fighter.setWeightClass(mapWeightClass(weightClass));
                    fighter.setActive(true);
                    
                    // Valores por defecto
                    fighter.setRecord("0-0");
                    fighter.setNationality("Unknown");
                    fighter.setAge(30);
                    fighter.setHeight(1.80);
                    fighter.setWeight(getDefaultWeightForClass(fighter.getWeightClass()));
                    
                    // Buscar página de perfil para obtener más datos
                    String profileUrl = findFighterProfileUrl(fighterName, fightBlockHtml);
                    if (profileUrl != null && !profileUrl.isEmpty()) {
                        try {
                            enrichFighterFromProfile(fighter, profileUrl);
                        } catch (Exception e) {
                            logger.warn("Error obteniendo detalles del perfil para {}: {}", fighterName, e.getMessage());
                        }
                    }
                    
                    // Establecer precio estándar
                    fighter.setBasePrice(60);
                    fighter.setPrice(60);
                    
                    fighters.add(fighter);
                }
            }
            
            logger.info("Extraídos {} luchadores del sitio web de UFC", fighters.size());
            
        } catch (Exception e) {
            logger.error("Error extrayendo luchadores desde UFC website: {}", e.getMessage(), e);
        }
        
        return fighters;
    }
    
    /**
     * Intenta encontrar la URL del perfil de un luchador
     */
    private String findFighterProfileUrl(String fighterName, String html) {
        try {
            // Buscar enlaces en el bloque HTML
            Pattern linkPattern = Pattern.compile("<a href=\"([^\"]+)\"[^>]*>\\s*" + Pattern.quote(fighterName) + "\\s*</a>");
            Matcher linkMatcher = linkPattern.matcher(html);
            
            if (linkMatcher.find()) {
                String profileUrl = linkMatcher.group(1);
                if (!profileUrl.startsWith("http")) {
                    // Si es una URL relativa, convertirla a absoluta
                    profileUrl = "https://www.ufcespanol.com" + profileUrl;
                }
                return profileUrl;
            }
            
            // Si no se encuentra, buscar de manera más flexible
            String cleanName = fighterName.toLowerCase()
                .replaceAll("[^a-z ]", "")
                .replaceAll("\\s+", "-");
            
            Pattern altLinkPattern = Pattern.compile("<a href=\"([^\"]+/" + cleanName + "[^\"]*)\">", Pattern.CASE_INSENSITIVE);
            Matcher altLinkMatcher = altLinkPattern.matcher(html);
            
            if (altLinkMatcher.find()) {
                String profileUrl = altLinkMatcher.group(1);
                if (!profileUrl.startsWith("http")) {
                    profileUrl = "https://www.ufcespanol.com" + profileUrl;
                }
                return profileUrl;
            }
        } catch (Exception e) {
            logger.warn("Error buscando URL de perfil para {}: {}", fighterName, e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Enriquece la información de un luchador desde su página de perfil
     */
    private void enrichFighterFromProfile(Fighter fighter, String profileUrl) throws Exception {
        String profileHtml = fetchWebContent(profileUrl);
        
        // Extraer récord
        extractRecord(fighter, profileHtml);
        
        // Extraer nacionalidad
        extractNationality(fighter, profileHtml);
        
        // Extraer edad
        extractAge(fighter, profileHtml);
        
        // Extraer altura
        extractHeight(fighter, profileHtml);
        
        // Extraer peso
        extractWeight(fighter, profileHtml);
    }
    
    /**
     * Extrae el récord del luchador del HTML
     */
    private void extractRecord(Fighter fighter, String html) {
        try {
            Pattern recordPattern = Pattern.compile("<div class=\"c-record__promoted\">(.*?)</div>");
            Matcher recordMatcher = recordPattern.matcher(html);
            if (recordMatcher.find()) {
                String recordText = recordMatcher.group(1).trim()
                    .replaceAll("<[^>]*>", "")
                    .replaceAll("\\s+", " ");
                
                // Formatear a "W-L" 
                if (recordText.contains("-")) {
                    fighter.setRecord(recordText);
                } else {
                    fighter.setRecord("0-0");
                }
            }
        } catch (Exception e) {
            logger.warn("Error extrayendo récord: {}", e.getMessage());
        }
    }
    
    /**
     * Extrae la nacionalidad del luchador del HTML
     */
    private void extractNationality(Fighter fighter, String html) {
        try {
            // Intentar extraer de "Lugar de nacimiento"
            Pattern countryPattern = Pattern.compile("<div class=\"c-bio__field\">\\s*<div class=\"c-bio__label\">Lugar de nacimiento</div>\\s*<div class=\"c-bio__text\">(.*?)</div>");
            Matcher countryMatcher = countryPattern.matcher(html);
            if (countryMatcher.find()) {
                String country = countryMatcher.group(1).trim();
                if (country.contains(",")) {
                    country = country.substring(country.lastIndexOf(",") + 1).trim();
                }
                fighter.setNationality(country);
                return;
            }
            
            // Intentar extraer del país de la bandera
            Pattern flagPattern = Pattern.compile("<div class=\"field field--name-country.*?title=\"([^\"]+)\"");
            Matcher flagMatcher = flagPattern.matcher(html);
            if (flagMatcher.find()) {
                fighter.setNationality(flagMatcher.group(1).trim());
            }
        } catch (Exception e) {
            logger.warn("Error extrayendo nacionalidad: {}", e.getMessage());
        }
    }
    
    /**
     * Extrae la edad del luchador del HTML
     */
    private void extractAge(Fighter fighter, String html) {
        try {
            Pattern agePattern = Pattern.compile("<div class=\"c-bio__field\">\\s*<div class=\"c-bio__label\">Edad</div>\\s*<div class=\"c-bio__text\">(\\d+)</div>");
            Matcher ageMatcher = agePattern.matcher(html);
            if (ageMatcher.find()) {
                fighter.setAge(Integer.parseInt(ageMatcher.group(1).trim()));
            }
        } catch (Exception e) {
            logger.warn("Error extrayendo edad: {}", e.getMessage());
        }
    }
    
    /**
     * Extrae la altura del luchador del HTML
     */
    private void extractHeight(Fighter fighter, String html) {
        try {
            Pattern heightPattern = Pattern.compile("<div class=\"c-bio__field\">\\s*<div class=\"c-bio__label\">Altura</div>\\s*<div class=\"c-bio__text\">(.*?)</div>");
            Matcher heightMatcher = heightPattern.matcher(html);
            if (heightMatcher.find()) {
                String heightText = heightMatcher.group(1).trim();
                
                // Convertir altura en el formato que venga
                if (heightText.contains("'")) {
                    // Formato pies/pulgadas (5' 10")
                    String[] parts = heightText.split("'");
                    int feet = Integer.parseInt(parts[0].trim());
                    int inches = Integer.parseInt(parts[1].replaceAll("\"", "").trim());
                    double heightInMeters = (feet * 30.48 + inches * 2.54) / 100; // Convertir a metros
                    fighter.setHeight(heightInMeters);
                } else if (heightText.contains("cm")) {
                    // Formato centímetros
                    double heightInCm = Double.parseDouble(heightText.replaceAll("[^0-9.]", ""));
                    fighter.setHeight(heightInCm / 100); // Convertir a metros
                }
            }
        } catch (Exception e) {
            logger.warn("Error extrayendo altura: {}", e.getMessage());
        }
    }
    
    /**
     * Extrae el peso del luchador del HTML
     */
    private void extractWeight(Fighter fighter, String html) {
        try {
            Pattern weightPattern = Pattern.compile("<div class=\"c-bio__field\">\\s*<div class=\"c-bio__label\">Peso</div>\\s*<div class=\"c-bio__text\">(.*?)</div>");
            Matcher weightMatcher = weightPattern.matcher(html);
            if (weightMatcher.find()) {
                String weightText = weightMatcher.group(1).trim();
                
                // Convertir el peso al formato que venga
                if (weightText.contains("lbs")) {
                    // Libras -> kg
                    double weightInLbs = Double.parseDouble(weightText.replaceAll("[^0-9.]", ""));
                    fighter.setWeight(weightInLbs * 0.453592); // Convertir a kg
                } else if (weightText.contains("kg")) {
                    // Ya está en kg
                    fighter.setWeight(Double.parseDouble(weightText.replaceAll("[^0-9.]", "")));
                }
            }
        } catch (Exception e) {
            logger.warn("Error extrayendo peso: {}", e.getMessage());
        }
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
     * Devuelve peso predeterminado para una categoría de peso
     */
    private double getDefaultWeightForClass(String weightClass) {
        weightClass = weightClass.toLowerCase();
        
        if (weightClass.contains("heavyweight")) return 120.0;
        if (weightClass.contains("light heavyweight")) return 93.0;
        if (weightClass.contains("middleweight")) return 84.0;
        if (weightClass.contains("welterweight")) return 77.0;
        if (weightClass.contains("lightweight")) return 70.0;
        if (weightClass.contains("featherweight")) return 66.0;
        if (weightClass.contains("bantamweight")) return 61.0;
        if (weightClass.contains("flyweight")) return 57.0;
        if (weightClass.contains("strawweight")) return 52.0;
        
        return 77.0; // Welterweight como valor por defecto
    }
    
    /**
     * Obtiene el contenido HTML de una URL
     */
    private String fetchWebContent(String urlString) throws Exception {
        StringBuilder content = new StringBuilder();
        
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        return content.toString();
    }
}