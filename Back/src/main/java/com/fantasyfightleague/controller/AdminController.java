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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
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
}