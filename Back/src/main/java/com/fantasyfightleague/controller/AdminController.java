package com.fantasyfightleague.controller;

import com.fantasyfightleague.scheduler.UFCSyncScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    private final UFCSyncScheduler ufcSyncScheduler;
    
    @Autowired
    public AdminController(UFCSyncScheduler ufcSyncScheduler) {
        this.ufcSyncScheduler = ufcSyncScheduler;
    }
    
    /**
     * Endpoint para sincronizar manualmente los luchadores de la UFC.
     */
    @PostMapping("/sync-fighters")
    public ResponseEntity<String> syncFighters() {
        try {
            ufcSyncScheduler.manualSync();
            return ResponseEntity.ok("Sincronización de luchadores iniciada correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al iniciar la sincronización: " + e.getMessage());
        }
    }
}