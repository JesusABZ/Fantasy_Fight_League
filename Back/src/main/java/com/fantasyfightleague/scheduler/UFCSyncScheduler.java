package com.fantasyfightleague.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Clase simplificada, mantiene solo la estructura básica del componente
 * sin funcionalidad de sincronización automatizada
 */
@Component
public class UFCSyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger(UFCSyncScheduler.class);
    
    /**
     * Constructor vacío
     */
    public UFCSyncScheduler() {
        logger.info("UFCSyncScheduler inicializado en modo manual");
    }
    
    /**
     * Método para posible sincronización manual
     * Ahora simplemente registra que se ha solicitado sin realizar acciones
     */
    public void manualSync() {
        logger.info("Solicitud de sincronización manual recibida - Deshabilitada");
        logger.info("Utilice el endpoint /api/admin/import-fighters para importar luchadores manualmente");
    }
}