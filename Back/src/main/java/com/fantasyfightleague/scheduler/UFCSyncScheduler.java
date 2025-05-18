package com.fantasyfightleague.scheduler;

import com.fantasyfightleague.service.SportradarService;
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
    
    private final SportradarService sportradarService;
    
    @Autowired
    public UFCSyncScheduler(SportradarService sportradarService) {
        this.sportradarService = sportradarService;
    }
    
    /**
     * Ejecuta la sincronización cada domingo a las 23:59 PM.
     * La expresión cron "0 59 23 ? * SUN" significa:
     * - 0 segundos
     * - 59 minutos
     * - 23 horas
     * - Cualquier día del mes
     * - Cualquier mes
     * - Domingo
     */
    @Scheduled(cron = "0 59 23 ? * SUN")
    public void scheduledSync() {
        logger.info("Iniciando sincronización programada para el próximo evento");
        sportradarService.syncNextEventFighters();
    }
    
    /**
     * Método para ejecutar la sincronización manualmente desde un controlador o durante el inicio.
     */
    public void manualSync() {
        logger.info("Iniciando sincronización manual");
        sportradarService.syncNextEventFighters();
    }
}