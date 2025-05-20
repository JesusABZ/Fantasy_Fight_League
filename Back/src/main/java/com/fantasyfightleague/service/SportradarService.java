package com.fantasyfightleague.service;

import com.fantasyfightleague.model.Fighter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SportradarService {

    private static final Logger logger = LoggerFactory.getLogger(SportradarService.class);
    
    private final FighterService fighterService;
    
    @Autowired
    public SportradarService(FighterService fighterService) {
        this.fighterService = fighterService;
    }
    
    /**
     * Desactiva todos los luchadores activos en la base de datos
     * @return Número de luchadores desactivados
     */
    public int deactivateAllFighters() {
        List<Fighter> activeFighters = fighterService.findAllActiveFighters();
        logger.info("Desactivando {} luchadores activos", activeFighters.size());
        
        for (Fighter fighter : activeFighters) {
            fighter.setActive(false);
            fighterService.saveFighter(fighter);
        }
        
        return activeFighters.size();
    }
}