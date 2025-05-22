// Crear: src/main/java/com/fantasyfightleague/dto/JoinLeagueDTO.java
package com.fantasyfightleague.dto;

public class JoinLeagueDTO {
    private String invitationCode;
    
    // Constructores
    public JoinLeagueDTO() {
    }
    
    public JoinLeagueDTO(String invitationCode) {
        this.invitationCode = invitationCode;
    }
    
    // Getters y setters
    public String getInvitationCode() {
        return invitationCode;
    }
    
    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }
}