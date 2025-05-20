package com.fantasyfightleague.controller;

import com.fantasyfightleague.model.Fighter;
import com.fantasyfightleague.scheduler.UFCSyncScheduler;
import com.fantasyfightleague.service.FighterService;
import com.fantasyfightleague.service.SportradarService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
    private final UFCSyncScheduler ufcSyncScheduler;
    private final FighterService fighterService;
    private final SportradarService sportradarService;
    
    @Autowired
    public AdminController(UFCSyncScheduler ufcSyncScheduler, 
                          FighterService fighterService,
                          SportradarService sportradarService) {
        this.ufcSyncScheduler = ufcSyncScheduler;
        this.fighterService = fighterService;
        this.sportradarService = sportradarService;
    }
    
    /**
     * Endpoint para sincronizar manualmente los luchadores del próximo evento UFC.
     */
    @PostMapping("/sync-fighters")
    public ResponseEntity<String> syncFighters() {
        try {
            ufcSyncScheduler.manualSync();
            return ResponseEntity.ok("Sincronización de luchadores del próximo evento UFC iniciada correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al iniciar la sincronización: " + e.getMessage());
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
     * Endpoint para sincronizar manualmente los luchadores desde una URL específica de UFC.
     */
    @PostMapping("/sync-fighters-from-url")
    public ResponseEntity<String> syncFightersFromUrl(@RequestParam("url") String url) {
        try {
            sportradarService.syncFightersFromUFCWebsite(url);
            return ResponseEntity.ok("Sincronización de luchadores desde " + url + " iniciada correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al iniciar la sincronización: " + e.getMessage());
        }
    }
    
    @PostMapping("/sync-fighters-with-logging")
    public ResponseEntity<String> syncFightersWithLogging(@RequestParam("url") String url) {
        try {
            logger.info("Iniciando sincronización de luchadores desde {} con logging detallado", url);
            
            // 1. Desactivar todos los luchadores actuales
            int deactivatedCount = sportradarService.deactivateAllFighters();
            logger.info("Desactivados {} luchadores anteriores", deactivatedCount);
            
            // 2. Sincronizar luchadores desde la URL
            sportradarService.syncFightersFromUFCWebsite(url);
            
            return ResponseEntity.ok("Sincronización de luchadores completada. Revisa los logs para ver el detalle.");
        } catch (Exception e) {
            logger.error("Error en la sincronización con logging: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}