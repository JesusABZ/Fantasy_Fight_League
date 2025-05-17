package com.fantasyfightleague.repository;

import com.fantasyfightleague.model.Fighter;
import com.fantasyfightleague.model.League;
import com.fantasyfightleague.model.MarketListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MarketListingRepository extends JpaRepository<MarketListing, Long> {
    
    // Buscar listados de mercado por liga
    List<MarketListing> findByLeague(League league);
    
    // Buscar listados activos
    List<MarketListing> findByStatus(String status);
    
    // Buscar listados activos en una liga
    List<MarketListing> findByLeagueAndStatus(League league, String status);
    
    // Buscar listados que vencen pronto
    List<MarketListing> findByStatusAndEndDateBefore(String status, Date date);
    
    // Buscar listados para un luchador específico
    List<MarketListing> findByFighter(Fighter fighter);
    
    // Buscar listados activos para un luchador en una liga
    List<MarketListing> findByLeagueAndFighterAndStatus(League league, Fighter fighter, String status);
}