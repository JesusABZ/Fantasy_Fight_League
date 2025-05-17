package com.fantasyfightleague.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class UFCSyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger(UFCSyncScheduler.class);
    
    private final UFCSyncService ufcSyncService;
    
    @Autowired
    public UFCSyncScheduler(UFCSyncService ufcSyncService) {
        this.ufcSyncService = ufcSyncService;
    }
    
    /**
     * Ejecuta la sincronización cada lunes a las 3:00 AM.
     * La expresión cron "0 0 3 ? * MON" significa:
     * - 0 segundos
     * - 0 minutos
     * - 3 horas
     * - Cualquier día del mes
     * - Cualquier mes
     * - Lunes
     */
    @Scheduled(cron = "0 0 3 ? * MON")
    public void scheduledSync() {
        logger.info("Iniciando sincronización programada");
        ufcSyncService.synchronizeFighters();
    }
    
    /**
     * Método para ejecutar la sincronización manualmente desde un controlador o durante el inicio.
     */
    public void manualSync() {
        logger.info("Iniciando sincronización manual");
        ufcSyncService.synchronizeFighters();
    }
}