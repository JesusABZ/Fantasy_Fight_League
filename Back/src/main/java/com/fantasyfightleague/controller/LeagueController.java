// Crear: src/main/java/com/fantasyfightleague/controller/LeagueController.java
package com.fantasyfightleague.controller;

import com.fantasyfightleague.dto.CreatePrivateLeagueDTO;
import com.fantasyfightleague.dto.CreatePublicLeagueDTO;
import com.fantasyfightleague.dto.JoinLeagueDTO;
import com.fantasyfightleague.dto.MessageResponseDTO;
import com.fantasyfightleague.model.Event;
import com.fantasyfightleague.model.League;
import com.fantasyfightleague.model.User;
import com.fantasyfightleague.service.EventService;
import com.fantasyfightleague.service.LeagueService;
import com.fantasyfightleague.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class LeagueController {
    
    @Autowired
    private LeagueService leagueService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EventService eventService;
    
    /**
     * SOLO ADMINS - Crear liga pública (asociada a un evento específico)
     */
    @PostMapping("/admin/leagues/public")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPublicLeague(@RequestBody CreatePublicLeagueDTO createDTO) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User creator = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            Event event = eventService.findById(createDTO.getEventId())
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
            
            // Crear liga con parámetros por defecto del DTO
            League league = new League();
            league.setName(createDTO.getName());
            league.setDescription(createDTO.getDescription());
            league.setType("PUBLIC");
            league.setEvent(event);
            league.setCreator(creator);
            league.setInitialBudget(createDTO.getInitialBudget());
            league.setMaxFighters(createDTO.getMaxFighters());
            league.setMaxFightersEvent(createDTO.getMaxFightersEvent());
            league.setMinFightersEvent(createDTO.getMinFightersEvent());
            
            // Configurar fecha de auto-eliminación (2 días después del evento)
            if (event.getEndDate() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(event.getEndDate());
                cal.add(Calendar.DAY_OF_MONTH, 2);
                league.setAutoDeleteDate(cal.getTime());
            }
            
            // El creador se une automáticamente
            league.addMember(creator);
            
            // Guardar usando el repositorio directamente para evitar problemas con el servicio
            League savedLeague = leagueService.joinLeague(
                leagueService.createPublicLeague(
                    createDTO.getName(),
                    createDTO.getDescription(),
                    event,
                    creator
                ), creator);
            
            return ResponseEntity.ok(savedLeague);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
    
    /**
     * USUARIOS - Crear liga privada
     */
    @PostMapping("/leagues/private")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createPrivateLeague(@RequestBody CreatePrivateLeagueDTO createDTO) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User creator = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Crear liga privada con parámetros del DTO
            League league = new League();
            league.setName(createDTO.getName());
            league.setDescription(createDTO.getDescription());
            league.setType("PRIVATE");
            league.setCreator(creator);
            league.setInitialBudget(createDTO.getInitialBudget());
            league.setMaxFighters(createDTO.getMaxFighters());
            league.setMaxFightersEvent(createDTO.getMaxFightersEvent());
            league.setMinFightersEvent(createDTO.getMinFightersEvent());
            league.setInvitationCode(leagueService.generateInvitationCode());
            
            // El creador se une automáticamente
            league.addMember(creator);
            
            League savedLeague = leagueService.joinLeague(
                leagueService.createPrivateLeague(
                    createDTO.getName(),
                    createDTO.getDescription(),
                    creator
                ), creator);
            
            return ResponseEntity.ok(savedLeague);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
    
    /**
     * PÚBLICO - Obtener ligas públicas activas
     */
    @GetMapping("/leagues/public")
    public ResponseEntity<List<League>> getActivePublicLeagues() {
        List<League> leagues = leagueService.findActivePublicLeagues();
        return ResponseEntity.ok(leagues);
    }
    
    /**
     * USUARIOS - Unirse a liga pública
     */
    @PostMapping("/leagues/{leagueId}/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> joinPublicLeague(@PathVariable Long leagueId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            League league = leagueService.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));
            
            if (!"PUBLIC".equals(league.getType())) {
                return ResponseEntity.badRequest().body(new MessageResponseDTO("Esta liga no es pública"));
            }
            
            if (leagueService.isUserInLeague(league, user)) {
                return ResponseEntity.badRequest().body(new MessageResponseDTO("Ya eres miembro de esta liga"));
            }
            
            League updatedLeague = leagueService.joinLeague(league, user);
            return ResponseEntity.ok(new MessageResponseDTO("Te has unido a la liga: " + updatedLeague.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
    
    /**
     * USUARIOS - Unirse a liga privada con código de invitación
     */
    @PostMapping("/leagues/join-private")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> joinPrivateLeague(@RequestBody JoinLeagueDTO joinDTO) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            League league = leagueService.findByInvitationCode(joinDTO.getInvitationCode())
                    .orElseThrow(() -> new RuntimeException("Código de invitación inválido"));
            
            if (leagueService.isUserInLeague(league, user)) {
                return ResponseEntity.badRequest().body(new MessageResponseDTO("Ya eres miembro de esta liga"));
            }
            
            League updatedLeague = leagueService.joinLeague(league, user);
            return ResponseEntity.ok(new MessageResponseDTO("Te has unido a la liga: " + updatedLeague.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
    
    /**
     * USUARIOS - Obtener mis ligas
     */
    @GetMapping("/leagues/my-leagues")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<League>> getMyLeagues() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            List<League> leagues = leagueService.findLeaguesByUser(user);
            return ResponseEntity.ok(leagues);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    /**
     * USUARIOS - Salir de una liga
     */
    @DeleteMapping("/leagues/{leagueId}/leave")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> leaveLeague(@PathVariable Long leagueId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            League league = leagueService.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));
            
            if (!leagueService.isUserInLeague(league, user)) {
                return ResponseEntity.badRequest().body(new MessageResponseDTO("No eres miembro de esta liga"));
            }
            
            if (league.getCreator().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body(new MessageResponseDTO("El creador no puede abandonar su propia liga"));
            }
            
            leagueService.leaveLeague(league, user);
            return ResponseEntity.ok(new MessageResponseDTO("Has abandonado la liga: " + league.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/leagues/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getLeagueDetails(@PathVariable Long id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            League league = leagueService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));
            
            // Verificar que el usuario es miembro de la liga
            if (!leagueService.isUserInLeague(league, user)) {
                return ResponseEntity.badRequest().body(new MessageResponseDTO("No eres miembro de esta liga"));
            }
            
            // Enriquecer los datos de la liga
            Map<String, Object> response = new HashMap<>();
            response.put("id", league.getId());
            response.put("name", league.getName());
            response.put("description", league.getDescription());
            response.put("type", league.getType());
            response.put("createdAt", league.getCreatedAt());
            response.put("initialBudget", league.getInitialBudget());
            response.put("maxFighters", league.getMaxFighters());
            response.put("maxFightersEvent", league.getMaxFightersEvent());
            response.put("minFightersEvent", league.getMinFightersEvent());
            response.put("invitationCode", league.getInvitationCode()); // Solo para privadas
            response.put("memberCount", league.getMembers().size());
            
            // Para ligas públicas, incluir el evento asociado
            if ("PUBLIC".equals(league.getType()) && league.getEvent() != null) {
                response.put("event", league.getEvent());
            }
            
            // Incluir información del creador
            Map<String, Object> creator = new HashMap<>();
            creator.put("id", league.getCreator().getId());
            creator.put("username", league.getCreator().getUsername());
            response.put("creator", creator);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
}