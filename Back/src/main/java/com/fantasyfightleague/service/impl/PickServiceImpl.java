package com.fantasyfightleague.service.impl;

import com.fantasyfightleague.model.*;
import com.fantasyfightleague.repository.PickRepository;
import com.fantasyfightleague.service.FighterService;
import com.fantasyfightleague.service.PickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PickServiceImpl implements PickService {
    
    @Autowired
    private PickRepository pickRepository;
    
    @Autowired
    private FighterService fighterService;
    
    @Override
    @Transactional
    public Pick createOrUpdatePick(User user, League league, Event event, List<Long> fighterIds) {
        // Validar luchadores
        List<Fighter> fighters = new ArrayList<>();
        for (Long fighterId : fighterIds) {
            Fighter fighter = fighterService.findById(fighterId)
                    .orElseThrow(() -> new RuntimeException("Luchador no encontrado: " + fighterId));
            
            // Verificar que el luchador está en el evento
            if (!event.getFighters().contains(fighter)) {
                throw new RuntimeException("El luchador " + fighter.getName() + " no participa en este evento");
            }
            
            fighters.add(fighter);
        }
        
        // Validar presupuesto
        if (!isValidPickCombination(fighters, league.getInitialBudget())) {
            throw new RuntimeException("El costo total de los luchadores excede el presupuesto de la liga");
        }
        
        // Buscar pick existente o crear uno nuevo
        Pick pick = pickRepository.findByUserAndLeagueAndEvent(user, league, event)
                .orElse(new Pick(user, league, event));
        
        // Verificar que se puede modificar
        if (!pick.canBeModified()) {
            throw new RuntimeException("No puedes modificar este pick (está bloqueado o pasó el deadline)");
        }
        
        // Limpiar luchadores anteriores y agregar los nuevos
        pick.getSelectedFighters().clear();
        for (Fighter fighter : fighters) {
            pick.addFighter(fighter);
        }
        
        return pickRepository.save(pick);
    }
    
    @Override
    public Optional<Pick> findById(Long id) {
        return pickRepository.findById(id);
    }
    
    @Override
    public Optional<Pick> findByUserLeagueAndEvent(User user, League league, Event event) {
        return pickRepository.findByUserAndLeagueAndEvent(user, league, event);
    }
    
    @Override
    public List<Pick> findByUserAndLeague(User user, League league) {
        return pickRepository.findByUserAndLeague(user, league);
    }
    
    @Override
    public List<Pick> findByLeagueAndEvent(League league, Event event) {
        return pickRepository.findByLeagueAndEvent(league, event);
    }
    
    @Override
    public List<Pick> getEventLeaderboard(League league, Event event) {
        return pickRepository.findByLeagueAndEventOrderByEventPointsDesc(league, event);
    }
    
    @Override
    public List<Map<String, Object>> getGlobalLeaderboard(League league) {
        List<Object[]> results = pickRepository.findGlobalLeaderboard(league);
        
        return results.stream().map(result -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("user", result[0]);
            entry.put("totalPoints", result[1]);
            return entry;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deletePick(Long pickId) {
        pickRepository.deleteById(pickId);
    }
    
    @Override
    @Transactional
    public int lockPicksForEvent(Event event) {
        List<Pick> picksToLock = pickRepository.findByLeagueAndEvent(null, event)
                .stream()
                .filter(pick -> !pick.isLocked())
                .collect(Collectors.toList());
        
        for (Pick pick : picksToLock) {
            pick.setLocked(true);
            pickRepository.save(pick);
        }
        
        return picksToLock.size();
    }
    
    @Override
    @Transactional
    public void lockExpiredPicks() {
        List<Pick> expiredPicks = pickRepository.findPicksToLock();
        
        for (Pick pick : expiredPicks) {
            pick.setLocked(true);
            pickRepository.save(pick);
        }
    }
    
    @Override
    public boolean canUserMakePick(User user, League league, Event event) {
        // Verificar que es miembro de la liga
        if (!league.getMembers().contains(user)) {
            return false;
        }
        
        // Verificar que los picks están abiertos
        return event.isPicksOpen();
    }
    
    @Override
    public boolean isValidPickCombination(List<Fighter> fighters, Integer budget) {
        if (fighters.isEmpty() || fighters.size() > 3) {
            return false;
        }
        
        int totalCost = fighters.stream()
                .mapToInt(Fighter::getPrice)
                .sum();
        
        return totalCost <= budget;
    }
}