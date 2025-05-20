package com.fantasyfightleague.controller;

import com.fantasyfightleague.dto.CompleteFighterDTO;
import com.fantasyfightleague.dto.FighterDTO;
import com.fantasyfightleague.dto.FighterImageDTO;
import com.fantasyfightleague.dto.FighterPriceDTO;
import com.fantasyfightleague.model.Fighter;
import com.fantasyfightleague.scheduler.UFCSyncScheduler;
import com.fantasyfightleague.service.FighterPricingService;
import com.fantasyfightleague.service.FighterService;
import com.fantasyfightleague.service.SportradarService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    private final UFCSyncScheduler ufcSyncScheduler;
    private final FighterService fighterService;
    private final SportradarService sportradarService;
    private final FighterPricingService fighterPricingService;
    
    @Autowired
    public AdminController(UFCSyncScheduler ufcSyncScheduler, 
                          FighterService fighterService,
                          SportradarService sportradarService,
                          FighterPricingService fighterPricingService) {
        this.ufcSyncScheduler = ufcSyncScheduler;
        this.fighterService = fighterService;
        this.sportradarService = sportradarService;
        this.fighterPricingService = fighterPricingService;
    }
    
    /**
     * Endpoint para importar luchadores manualmente mediante un JSON
     */
    @PostMapping("/import-fighters")
    public ResponseEntity<String> importFighters(@RequestBody List<FighterDTO> fighterDTOs) {
        try {
            logger.info("Iniciando importación manual de {} luchadores", fighterDTOs.size());
            
            // 1. Desactivar todos los luchadores actuales
            int deactivatedCount = sportradarService.deactivateAllFighters();
            logger.info("Desactivados {} luchadores anteriores", deactivatedCount);
            
            // 2. Procesar e importar los nuevos luchadores
            int importedCount = 0;
            
            for (FighterDTO dto : fighterDTOs) {
                // Buscar si ya existe un luchador con ese nombre
                List<Fighter> existingFighters = fighterService.findByName(dto.getName());
                
                if (!existingFighters.isEmpty()) {
                    // Actualizar luchador existente
                    Fighter existingFighter = existingFighters.get(0);
                    existingFighter.setRecord(dto.getRecord());
                    existingFighter.setNationality(dto.getNationality());
                    existingFighter.setWeightClass(dto.getWeightClass());
                    existingFighter.setActive(true);
                    
                    fighterService.saveFighter(existingFighter);
                    logger.info("Actualizado luchador existente: {}", dto.getName());
                } else {
                    // Crear nuevo luchador
                    Fighter fighter = new Fighter();
                    fighter.setName(dto.getName());
                    fighter.setRecord(dto.getRecord());
                    fighter.setNationality(dto.getNationality());
                    fighter.setWeightClass(dto.getWeightClass());
                    fighter.setActive(true);
                    fighter.setPrice(60); // Precio por defecto
                    
                    fighterService.saveFighter(fighter);
                    logger.info("Creado nuevo luchador: {}", dto.getName());
                }
                
                importedCount++;
            }
            
            return ResponseEntity.ok("Proceso completado. Luchadores desactivados: " + deactivatedCount + 
                                    ", Luchadores importados/actualizados: " + importedCount);
        } catch (Exception e) {
            logger.error("Error al importar luchadores: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para obtener luchadores activos.
     */
    @GetMapping("/active-fighters")
    public ResponseEntity<List<Fighter>> getActiveFighters() {
        try {
            List<Fighter> activeFighters = fighterService.findAllActiveFighters();
            return ResponseEntity.ok(activeFighters);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    /**
     * Endpoint para desactivar manualmente todos los luchadores.
     */
    @PostMapping("/deactivate-fighters")
    public ResponseEntity<String> deactivateFighters() {
        try {
            int count = sportradarService.deactivateAllFighters();
            return ResponseEntity.ok("Se han desactivado " + count + " luchadores correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al desactivar luchadores: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para actualizar la imagen de un luchador
     */
    @PostMapping("/update-fighter-image")
    public ResponseEntity<String> updateFighterImage(@RequestBody FighterImageDTO imageDTO) {
        try {
            Optional<Fighter> fighterOpt = fighterService.findById(imageDTO.getFighterId());
            
            if (fighterOpt.isPresent()) {
                Fighter fighter = fighterOpt.get();
                fighter.setImageUrl(imageDTO.getImageUrl());
                fighterService.saveFighter(fighter);
                
                logger.info("Actualizada imagen del luchador {} (ID: {})",
                        fighter.getName(), fighter.getId());
                
                return ResponseEntity.ok("Imagen actualizada correctamente para el luchador: " 
                        + fighter.getName());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró el luchador con ID: " + imageDTO.getFighterId());
            }
        } catch (Exception e) {
            logger.error("Error al actualizar la imagen del luchador: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para actualizar la imagen de un luchador por su ID
     */
    @PostMapping("/fighters/{id}/image")
    public ResponseEntity<String> updateFighterImageById(
            @PathVariable("id") Long fighterId,
            @RequestParam("imageUrl") String imageUrl) {
        try {
            Optional<Fighter> fighterOpt = fighterService.findById(fighterId);
            
            if (fighterOpt.isPresent()) {
                Fighter fighter = fighterOpt.get();
                fighter.setImageUrl(imageUrl);
                fighterService.saveFighter(fighter);
                
                logger.info("Actualizada imagen del luchador {} (ID: {})",
                        fighter.getName(), fighter.getId());
                
                return ResponseEntity.ok("Imagen actualizada correctamente para el luchador: " 
                        + fighter.getName());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró el luchador con ID: " + fighterId);
            }
        } catch (Exception e) {
            logger.error("Error al actualizar la imagen del luchador: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para actualizar el precio de un luchador manualmente
     */
    @PostMapping("/update-fighter-price")
    public ResponseEntity<String> updateFighterPrice(@RequestBody FighterPriceDTO priceDTO) {
        try {
            Optional<Fighter> fighterOpt = fighterService.findById(priceDTO.getFighterId());
            
            if (fighterOpt.isPresent()) {
                Fighter fighter = fighterOpt.get();
                
                // Calcular el precio basado en el DTO
                int newPrice = fighterPricingService.calculatePriceFromDTO(priceDTO);
                fighter.setPrice(newPrice);
                
                fighterService.saveFighter(fighter);
                
                logger.info("Actualizado precio del luchador {} (ID: {}) a {}",
                        fighter.getName(), fighter.getId(), fighter.getPrice());
                
                return ResponseEntity.ok("Precio actualizado correctamente para el luchador: " 
                        + fighter.getName() + " - Nuevo precio: " + fighter.getPrice());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró el luchador con ID: " + priceDTO.getFighterId());
            }
        } catch (Exception e) {
            logger.error("Error al actualizar el precio del luchador: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para actualizar el precio de un luchador por su ID
     */
    @PostMapping("/fighters/{id}/price")
    public ResponseEntity<String> updateFighterPriceById(
            @PathVariable("id") Long fighterId,
            @RequestParam("price") Integer price) {
        try {
            Optional<Fighter> fighterOpt = fighterService.findById(fighterId);
            
            if (fighterOpt.isPresent()) {
                Fighter fighter = fighterOpt.get();
                fighter.setPrice(price);
                fighterService.saveFighter(fighter);
                
                logger.info("Actualizado precio del luchador {} (ID: {}) a {}",
                        fighter.getName(), fighter.getId(), price);
                
                return ResponseEntity.ok("Precio actualizado correctamente para el luchador: " 
                        + fighter.getName() + " - Nuevo precio: " + price);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró el luchador con ID: " + fighterId);
            }
        } catch (Exception e) {
            logger.error("Error al actualizar el precio del luchador: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para calcular y actualizar los precios de múltiples luchadores para un evento
     */
    @PostMapping("/update-event-prices")
    public ResponseEntity<String> updateEventPrices(@RequestBody List<FighterPriceDTO> pricesList) {
        try {
            int updatedCount = 0;
            StringBuilder results = new StringBuilder("Resultados de la actualización de precios:\n");
            
            for (FighterPriceDTO priceDTO : pricesList) {
                Optional<Fighter> fighterOpt = fighterService.findById(priceDTO.getFighterId());
                
                if (fighterOpt.isPresent()) {
                    Fighter fighter = fighterOpt.get();
                    
                    // Calcular el precio basado en el DTO
                    int newPrice = fighterPricingService.calculatePriceFromDTO(priceDTO);
                    fighter.setPrice(newPrice);
                    
                    fighterService.saveFighter(fighter);
                    
                    results.append("- ")
                           .append(fighter.getName())
                           .append(": ")
                           .append(newPrice)
                           .append(" (");
                    
                    // Añadir información sobre el ranking para mayor claridad
                    if (priceDTO.getRanking() != null && !priceDTO.getRanking().isEmpty()) {
                        results.append("Ranking: ")
                               .append(priceDTO.getRanking())
                               .append(", ");
                    }
                    
                    results.append("Posición: ")
                           .append(priceDTO.getPosition())
                           .append(", ")
                           .append(priceDTO.getIsFavorite() != null && priceDTO.getIsFavorite() ? "Favorito" : "Underdog")
                           .append(")\n");
                    
                    updatedCount++;
                } else {
                    results.append("- Error: No se encontró el luchador con ID: ")
                           .append(priceDTO.getFighterId())
                           .append("\n");
                }
            }
            
            logger.info("Actualización masiva de precios completada. {} luchadores actualizados", updatedCount);
            
            return ResponseEntity.ok("Se actualizaron " + updatedCount + " luchadores\n" + results.toString());
        } catch (Exception e) {
            logger.error("Error al actualizar precios de luchadores: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/import-complete-fighters")
    public ResponseEntity<String> importCompleteFighters(@RequestBody List<CompleteFighterDTO> fighterDTOs) {
        try {
            logger.info("Iniciando importación completa de {} luchadores", fighterDTOs.size());
            
            // 1. Desactivar todos los luchadores actuales si se está haciendo una importación completa
            int deactivatedCount = sportradarService.deactivateAllFighters();
            logger.info("Desactivados {} luchadores anteriores", deactivatedCount);
            
            // 2. Procesar e importar los nuevos luchadores
            int importedCount = 0;
            StringBuilder results = new StringBuilder("Resultados de la importación:\n");
            
            for (CompleteFighterDTO dto : fighterDTOs) {
                // Buscar si ya existe un luchador con ese nombre
                List<Fighter> existingFighters = fighterService.findByName(dto.getName());
                Fighter fighter;
                boolean isNewFighter = existingFighters.isEmpty();
                
                if (!isNewFighter) {
                    // Actualizar luchador existente
                    fighter = existingFighters.get(0);
                    fighter.setRecord(dto.getRecord());
                    fighter.setNationality(dto.getNationality());
                    fighter.setWeightClass(dto.getWeightClass());
                    fighter.setActive(true);
                    
                    results.append("- Actualizado: ").append(dto.getName()).append("\n");
                } else {
                    // Crear nuevo luchador
                    fighter = new Fighter();
                    fighter.setName(dto.getName());
                    fighter.setRecord(dto.getRecord());
                    fighter.setNationality(dto.getNationality());
                    fighter.setWeightClass(dto.getWeightClass());
                    fighter.setActive(true);
                    
                    results.append("- Creado: ").append(dto.getName()).append("\n");
                }
                
                // Establecer la imagen si se proporciona
                if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
                    fighter.setImageUrl(dto.getImageUrl());
                    results.append("   Imagen: ").append(dto.getImageUrl()).append("\n");
                }
                
                // Calcular y establecer el precio
                int price;
                if (dto.getPrice() != null) {
                    // Si se proporciona un precio explícito, lo usamos
                    price = dto.getPrice();
                    results.append("   Precio explícito: ").append(price).append("\n");
                } else if (dto.getPosition() != null) {
                    // Si no hay precio pero sí datos para calcularlo, usamos el servicio de cálculo
                    FighterPriceDTO priceDTO = new FighterPriceDTO();
                    priceDTO.setPosition(dto.getPosition());
                    priceDTO.setIsFavorite(dto.getIsFavorite());
                    priceDTO.setRanking(dto.getRanking());
                    
                    price = fighterPricingService.calculatePriceFromDTO(priceDTO);
                    results.append("   Precio calculado: ").append(price)
                           .append(" (Posición: ").append(dto.getPosition())
                           .append(", Favorito: ").append(dto.getIsFavorite())
                           .append(", Ranking: ").append(dto.getRanking()).append(")\n");
                } else if (isNewFighter) {
                    // Si es un nuevo luchador y no hay datos de precio, usamos un valor por defecto
                    price = 60; // Precio por defecto para nuevos luchadores
                    results.append("   Precio por defecto: ").append(price).append("\n");
                } else {
                    // Si es un luchador existente y no hay datos de precio, mantenemos el actual
                    price = fighter.getPrice() != null ? fighter.getPrice() : 60;
                    results.append("   Precio mantenido: ").append(price).append("\n");
                }
                
                fighter.setPrice(price);
                
                // Guardar el luchador
                fighterService.saveFighter(fighter);
                importedCount++;
            }
            
            logger.info("Importación completa finalizada. {} luchadores importados/actualizados", importedCount);
            
            return ResponseEntity.ok("Proceso completado. Luchadores desactivados: " + deactivatedCount + 
                                    ", Luchadores importados/actualizados: " + importedCount + "\n" + results.toString());
        } catch (Exception e) {
            logger.error("Error al importar luchadores completos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}