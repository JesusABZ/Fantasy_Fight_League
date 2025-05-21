// Crear este archivo en: src/main/java/com/fantasyfightleague/dto/MessageResponseDTO.java
package com.fantasyfightleague.dto;

public class MessageResponseDTO {
    private String message;

    public MessageResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}