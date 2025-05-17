package com.fantasyfightleague.service.impl;

import com.fantasyfightleague.model.Fighter;
import com.fantasyfightleague.repository.FighterRepository;
import com.fantasyfightleague.service.FighterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FighterServiceImpl implements FighterService {
    
    private final FighterRepository fighterRepository;
    
    @Autowired
    public FighterServiceImpl(FighterRepository fighterRepository) {
        this.fighterRepository = fighterRepository;
    }
    
    @Override
    public Fighter saveFighter(Fighter fighter) {
        return fighterRepository.save(fighter);
    }
    
    @Override
    public Optional<Fighter> findById(Long id) {
        return fighterRepository.findById(id);
    }
    
    @Override
    public List<Fighter> findByWeightClass(String weightClass) {
        return fighterRepository.findByWeightClass(weightClass);
    }
    
    @Override
    public List<Fighter> findByName(String name) {
        return fighterRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    public List<Fighter> findByPriceRange(Integer minPrice, Integer maxPrice) {
        return fighterRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    @Override
    public List<Fighter> findAllFighters() {
        return fighterRepository.findAll();
    }
    
    @Override
    public List<Fighter> findAllActiveFighters() {
        return fighterRepository.findByActiveTrue();
    }
    
    @Override
    public void deleteFighter(Long id) {
        fighterRepository.deleteById(id);
    }
    
    @Override
    public Fighter updatePrice(Long id, Integer price) {
        Fighter fighter = fighterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fighter not found with id: " + id));
        fighter.setPrice(price);
        return fighterRepository.save(fighter);
    }
    
    @Override
    public Fighter deactivateFighter(Long id) {
        Fighter fighter = fighterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fighter not found with id: " + id));
        fighter.setActive(false);
        return fighterRepository.save(fighter);
    }
}