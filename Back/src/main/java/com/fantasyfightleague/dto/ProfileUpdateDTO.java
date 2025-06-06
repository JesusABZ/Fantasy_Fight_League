package com.fantasyfightleague.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

public class ProfileUpdateDTO {
    private String firstName;
    private String lastName;
    
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String profileImageUrl;
    
    // Getters y setters...
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}