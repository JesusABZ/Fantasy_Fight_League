// Actualizar: src/main/java/com/fantasyfightleague/service/impl/LeagueServiceImpl.java
package com.fantasyfightleague.service.impl;

import com.fantasyfightleague.model.Event;
import com.fantasyfightleague.model.League;
import com.fantasyfightleague.model.User;
import com.fantasyfightleague.repository.LeagueRepository;
import com.fantasyfightleague.service.LeagueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LeagueServiceImpl implements LeagueService {
    
    private static final Logger logger = LoggerFactory.getLogger(LeagueServiceImpl.class);
    
    private final LeagueRepository leagueRepository;
    
    @Autowired
    public LeagueServiceImpl(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }
    
    @Override
    public League createPublicLeague(String name, String description, Event event, User creator) {
        League league = new League();
        league.setName(name);
        league.setDescription(description);
        league.setType("PUBLIC");
        league.setEvent(event);
        league.setCreator(creator);
        
        // Las ligas públicas se eliminan automáticamente 2 días después del evento
        Calendar cal = Calendar.getInstance();
        cal.setTime(event.getEndDate());
        cal.add(Calendar.DAY_OF_MONTH, 2);
        league.setAutoDeleteDate(cal.getTime());
        
        // El creador se une automáticamente
        league.addMember(creator);
        
        return leagueRepository.save(league);
    }
    
    @Override
    public League createPrivateLeague(String name, String description, User creator) {
        League league = new League();
        league.setName(name);
        league.setDescription(description);
        league.setType("PRIVATE");
        league.setCreator(creator);
        league.setInvitationCode(generateInvitationCode());
        
        // El creador se une automáticamente
        league.addMember(creator);
        
        return leagueRepository.save(league);
    }
    
    @Override
    public Optional<League> findById(Long id) {
        return leagueRepository.findById(id);
    }
    
    @Override
    public List<League> findPublicLeagues() {
        return leagueRepository.findByTypeAndActiveTrue("PUBLIC");
    }
    
    @Override
    public List<League> findActivePublicLeagues() {
        return leagueRepository.findByTypeAndActiveTrueAndAutoDeleteDateAfter("PUBLIC", new Date());
    }
    
    @Override
    public List<League> findPrivateLeaguesByCreator(User creator) {
        return leagueRepository.findByCreatorAndTypeAndActiveTrue(creator, "PRIVATE");
    }
    
    @Override
    public List<League> findLeaguesByUser(User user) {
        return leagueRepository.findByMembersContaining(user);
    }
    
    @Override
    public Optional<League> findByInvitationCode(String invitationCode) {
        return Optional.ofNullable(leagueRepository.findByInvitationCodeAndActiveTrue(invitationCode));
    }
    
    @Override
    public League joinLeague(League league, User user) {
        if (!isUserInLeague(league, user)) {
            league.addMember(user);
            return leagueRepository.save(league);
        }
        return league;
    }
    
    // 🆕 MÉTODO MEJORADO: Ahora maneja la eliminación automática de ligas privadas vacías
    @Override
    @Transactional
    public League leaveLeague(League league, User user) {
        if (isUserInLeague(league, user)) {
            logger.info("Usuario {} saliendo de la liga {} ({})", 
                       user.getUsername(), league.getName(), league.getType());
            
            // Remover el usuario de la liga
            league.removeMember(user);
            
            // 🔥 NUEVO: Verificar si la liga privada se quedó sin miembros
            if ("PRIVATE".equals(league.getType()) && league.getMembers().isEmpty()) {
                logger.info("Liga privada {} se quedó sin miembros, eliminando...", league.getName());
                
                // Marcar como inactiva en lugar de eliminar físicamente por integridad referencial
                league.setActive(false);
                
                // Opcional: También podríamos eliminar físicamente si no hay dependencias
                // leagueRepository.delete(league);
                
                League savedLeague = leagueRepository.save(league);
                logger.info("Liga privada {} marcada como inactiva", league.getName());
                
                return savedLeague;
            } else {
                // Liga aún tiene miembros, solo guardar
                League savedLeague = leagueRepository.save(league);
                logger.info("Usuario {} removido de la liga {}. Miembros restantes: {}", 
                           user.getUsername(), league.getName(), savedLeague.getMembers().size());
                
                return savedLeague;
            }
        }
        return league;
    }
    
    @Override
    public boolean isUserInLeague(League league, User user) {
        return league.getMembers().contains(user);
    }
    
    @Override
    public void deleteLeague(Long id) {
        leagueRepository.deleteById(id);
    }
    
    @Override
    public void deleteExpiredPublicLeagues() {
        List<League> expiredLeagues = findExpiredPublicLeagues();
        for (League league : expiredLeagues) {
            league.setActive(false);
            leagueRepository.save(league);
        }
    }
    
    @Override
    public List<League> findExpiredPublicLeagues() {
        return leagueRepository.findByTypeAndActiveTrueAndAutoDeleteDateBefore("PUBLIC", new Date());
    }
    
    // 🆕 NUEVO MÉTODO: Encontrar ligas privadas vacías para limpieza
    public List<League> findEmptyPrivateLeagues() {
        List<League> allPrivateLeagues = leagueRepository.findByTypeAndActiveTrue("PRIVATE");
        return allPrivateLeagues.stream()
                .filter(league -> league.getMembers().isEmpty())
                .toList();
    }
    
    // 🆕 NUEVO MÉTODO: Limpiar ligas privadas vacías
    @Transactional
    public int cleanupEmptyPrivateLeagues() {
        List<League> emptyLeagues = findEmptyPrivateLeagues();
        int cleanedCount = 0;
        
        for (League league : emptyLeagues) {
            logger.info("Limpiando liga privada vacía: {}", league.getName());
            league.setActive(false);
            leagueRepository.save(league);
            cleanedCount++;
        }
        
        logger.info("Limpiadas {} ligas privadas vacías", cleanedCount);
        return cleanedCount;
    }
    
    @Override
    public String generateInvitationCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (leagueRepository.findByInvitationCode(code) != null);
        
        return code;
    }
    
    @Override
    public boolean isValidInvitationCode(String code) {
        return code != null && code.length() == 8 && code.matches("[A-Z0-9]+");
    }
}