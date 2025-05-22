// Crear: src/main/java/com/fantasyfightleague/service/impl/LeagueServiceImpl.java
package com.fantasyfightleague.service.impl;

import com.fantasyfightleague.model.Event;
import com.fantasyfightleague.model.League;
import com.fantasyfightleague.model.User;
import com.fantasyfightleague.repository.LeagueRepository;
import com.fantasyfightleague.service.LeagueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LeagueServiceImpl implements LeagueService {
    
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
        cal.setTime(event.getDate());
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
    
    @Override
    public League leaveLeague(League league, User user) {
        if (isUserInLeague(league, user)) {
            league.removeMember(user);
            return leagueRepository.save(league);
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