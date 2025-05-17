package com.fantasyfightleague.scheduler;

import com.fantasyfightleague.model.Fighter;
import com.fantasyfightleague.service.FighterService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UFCSyncService {
    
    private static final Logger logger = LoggerFactory.getLogger(UFCSyncService.class);
    private static final String FIGHTING_TOMATOES_API_BASE_URL = "https://fightingtomatoes.com/API";
    
    private final FighterService fighterService;
    private final RestTemplate restTemplate;
    
    @Value("${fightingtomatoes.api.username}")
    private String apiUsername;
    
    @Value("${fightingtomatoes.api.password}")
    private String apiPassword;
    
    @Autowired
    public UFCSyncService(FighterService fighterService, RestTemplate restTemplate) {
        this.fighterService = fighterService;
        this.restTemplate = restTemplate;
    }
    
    /**
     * Sincroniza los datos de los luchadores de la UFC con nuestra base de datos.
     */
    public void synchronizeFighters() {
        try {
            logger.info("Iniciando sincronización de luchadores de la UFC");
            
            // 1. Obtener luchadores de la API de Fighting Tomatoes
            List<Fighter> apiFighters = fetchFightersFromAPI();
            
            // 2. Obtener luchadores actuales de nuestra base de datos
            List<Fighter> currentFighters = fighterService.findAllFighters();
            
            // 3. Crear un mapa de luchadores actuales por nombre para búsqueda rápida
            Map<String, Fighter> currentFightersMap = new HashMap<>();
            for (Fighter fighter : currentFighters) {
                currentFightersMap.put(fighter.getName(), fighter);
            }
            
            // 4. Procesar los luchadores: añadir nuevos o actualizar existentes
            int added = 0;
            int updated = 0;
            
            for (Fighter apiFighter : apiFighters) {
                // Verificar si el luchador ya existe en nuestra base de datos
                if (currentFightersMap.containsKey(apiFighter.getName())) {
                    // Actualizar luchador existente
                    Fighter existingFighter = currentFightersMap.get(apiFighter.getName());
                    
                    // Actualizar solo si hay cambios
                    boolean hasChanges = false;
                    
                    if (!existingFighter.getWeightClass().equals(apiFighter.getWeightClass())) {
                        existingFighter.setWeightClass(apiFighter.getWeightClass());
                        hasChanges = true;
                    }
                    
                    if (apiFighter.getRecord() != null && !apiFighter.getRecord().equals(existingFighter.getRecord())) {
                        existingFighter.setRecord(apiFighter.getRecord());
                        hasChanges = true;
                    }
                    
                    if (apiFighter.getNationality() != null && !apiFighter.getNationality().equals(existingFighter.getNationality())) {
                        existingFighter.setNationality(apiFighter.getNationality());
                        hasChanges = true;
                    }
                    
                    // Actualizar otros campos relevantes
                    if (apiFighter.getAge() != null && !apiFighter.getAge().equals(existingFighter.getAge())) {
                        existingFighter.setAge(apiFighter.getAge());
                        hasChanges = true;
                    }
                    
                    if (apiFighter.getHeight() != null && !apiFighter.getHeight().equals(existingFighter.getHeight())) {
                        existingFighter.setHeight(apiFighter.getHeight());
                        hasChanges = true;
                    }
                    
                    if (apiFighter.getWeight() != null && !apiFighter.getWeight().equals(existingFighter.getWeight())) {
                        existingFighter.setWeight(apiFighter.getWeight());
                        hasChanges = true;
                    }
                    
                    if (apiFighter.getImageUrl() != null && !apiFighter.getImageUrl().equals(existingFighter.getImageUrl())) {
                        existingFighter.setImageUrl(apiFighter.getImageUrl());
                        hasChanges = true;
                    }
                    
                    if (hasChanges) {
                        fighterService.saveFighter(existingFighter);
                        updated++;
                        logger.info("Luchador actualizado: {}", existingFighter.getName());
                    }
                    
                    // Marcar como procesado
                    currentFightersMap.remove(apiFighter.getName());
                } else {
                    // Añadir nuevo luchador
                    // Asignar un precio base basado en sus estadísticas
                    int basePrice = calculateBasePrice(apiFighter);
                    apiFighter.setBasePrice(basePrice);
                    apiFighter.setPrice(basePrice);
                    
                    fighterService.saveFighter(apiFighter);
                    added++;
                    logger.info("Nuevo luchador añadido: {}", apiFighter.getName());
                }
            }
            
            // 5. Los luchadores que quedan en el mapa ya no están en la UFC
            int removed = 0;
            for (Fighter obsoleteFighter : currentFightersMap.values()) {
                // Marcar como inactivo
                obsoleteFighter.setActive(false);
                fighterService.saveFighter(obsoleteFighter);
                removed++;
                logger.info("Luchador marcado como inactivo: {}", obsoleteFighter.getName());
            }
            
            logger.info("Sincronización completada. Añadidos: {}, Actualizados: {}, Eliminados: {}", 
                    added, updated, removed);
            
        } catch (Exception e) {
            logger.error("Error durante la sincronización de luchadores: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene los datos de los luchadores desde la API de Fighting Tomatoes.
     */
    private List<Fighter> fetchFightersFromAPI() {
        List<Fighter> fighters = new ArrayList<>();
        
        try {
            // Obtener el año actual
            int currentYear = Year.now().getValue();
            
            // Construir la URL con el año actual
            String apiUrl = FIGHTING_TOMATOES_API_BASE_URL + "/fighters/" + currentYear;
            
            logger.info("Consultando API para el año {}: {}", currentYear, apiUrl);
            
            // Configurar autenticación
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(apiUsername, apiPassword);
            
            // Crear la entidad HTTP con los headers
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Realizar la solicitud GET con autenticación
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, 
                HttpMethod.GET,
                entity,
                String.class
            );
            
            // Procesar la respuesta
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONArray jsonArray = new JSONArray(response.getBody());
                
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonFighter = jsonArray.getJSONObject(i);
                    
                    if (jsonFighter.getString("promotion").equals("UFC")) {
                        Fighter fighter = new Fighter();
                        
                        // Nombre y categoría de peso son obligatorios
                        fighter.setName(jsonFighter.getString("name"));
                        fighter.setWeightClass(mapWeightClass(jsonFighter.getString("weight_class")));
                        
                        // Campos opcionales
                        if (!jsonFighter.isNull("record")) {
                            fighter.setRecord(jsonFighter.getString("record"));
                        }
                        
                        if (!jsonFighter.isNull("country")) {
                            fighter.setNationality(jsonFighter.getString("country"));
                        }
                        
                        if (!jsonFighter.isNull("age")) {
                            fighter.setAge(jsonFighter.getInt("age"));
                        }
                        
                        if (!jsonFighter.isNull("height_cm")) {
                            fighter.setHeight(jsonFighter.getDouble("height_cm") / 100); // Convertir cm a metros
                        }
                        
                        if (!jsonFighter.isNull("weight_kg")) {
                            fighter.setWeight(jsonFighter.getDouble("weight_kg"));
                        }
                        
                        if (!jsonFighter.isNull("image_url")) {
                            fighter.setImageUrl(jsonFighter.getString("image_url"));
                        }
                        
                        fighter.setActive(true); // Todos los luchadores de la API están activos
                        
                        fighters.add(fighter);
                    }
                }
                
                logger.info("Obtenidos {} luchadores de la API", fighters.size());
            } else {
                logger.error("Error en la respuesta de la API: {}", response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("Error obteniendo datos de la API: {}", e.getMessage(), e);
        }
        
        return fighters;
    }
    
    /**
     * Mapea la categoría de peso de la API al formato estándar de la UFC.
     */
    private String mapWeightClass(String apiWeightClass) {
        switch (apiWeightClass.toLowerCase()) {
            case "flyweight": return "Flyweight";
            case "bantamweight": return "Bantamweight";
            case "featherweight": return "Featherweight";
            case "lightweight": return "Lightweight";
            case "welterweight": return "Welterweight";
            case "middleweight": return "Middleweight";
            case "light_heavyweight": return "Light Heavyweight";
            case "heavyweight": return "Heavyweight";
            case "women's_strawweight": return "Women's Strawweight";
            case "women's_flyweight": return "Women's Flyweight";
            case "women's_bantamweight": return "Women's Bantamweight";
            case "women's_featherweight": return "Women's Featherweight";
            default: return apiWeightClass;
        }
    }
    
    /**
     * Calcula el precio base para un luchador basado en sus estadísticas.
     */
    private int calculateBasePrice(Fighter fighter) {
        int basePrice = 50; // Precio base mínimo
        
        // Ajustar según categoría de peso
        String weightClass = fighter.getWeightClass().toLowerCase();
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
                    logger.warn("Error parsing record for fighter: {}", fighter.getName());
                }
            }
        }
        
        // Limitar el precio entre 50 y 100
        return Math.min(100, Math.max(50, basePrice));
    }
}