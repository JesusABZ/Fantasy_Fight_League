package com.fantasyfightleague.service;

import com.fantasyfightleague.dto.FighterPriceDTO;
import com.fantasyfightleague.model.Fighter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio para calcular precios de luchadores basado en varios factores
 * Ajustado para equipos de 1-3 luchadores con presupuesto de 100,000
 * Los precios coinciden exactamente con la tabla de referencia
 */
@Service
public class FighterPricingService {
    
    private static final Logger logger = LoggerFactory.getLogger(FighterPricingService.class);
    
    // Constantes para el cálculo de precios ajustados para 1-3 luchadores por equipo
    private static final int BASE_PRICE = 20000; // Precio base mayor para ajustar a menos luchadores
    private static final int MAX_PRICE = 75000; // Precio máximo posible (75% del presupuesto total)
    
    /**
     * Calcula el precio basado en un DTO con información de ranking específica
     * @param priceDTO DTO con la información de precio
     * @return Precio calculado
     */
    public int calculatePriceFromDTO(FighterPriceDTO priceDTO) {
        // Si se proporciona un precio directo, usamos ese
        if (priceDTO.getPrice() != null) {
            return priceDTO.getPrice();
        }
        
        // Obtenemos los valores del DTO o valores predeterminados si son nulos
        String position = priceDTO.getPosition() != null ? priceDTO.getPosition().toUpperCase() : "PRELIMS";
        String ranking = priceDTO.getRanking();
        boolean isFavorite = priceDTO.getIsFavorite() != null ? priceDTO.getIsFavorite() : false;
        
        // Utilizamos la tabla de precios exactos
        return getPriceFromTable(position, ranking, isFavorite);
    }
    
    /**
     * Versión simplificada para calcular precios básicos
     * @param position Posición en la cartelera
     * @param ranking Ranking específico ("C", "1", "2", etc.)
     * @param isFavorite Si es favorito
     * @return Precio calculado según la tabla
     */
    public int calculateSimplePrice(String position, String ranking, boolean isFavorite) {
        return getPriceFromTable(position, ranking, isFavorite);
    }
    
    /**
     * Obtiene el precio directamente de la tabla de referencia
     * @param position Posición en la cartelera
     * @param ranking Ranking específico
     * @param isFavorite Si es favorito
     * @return Precio según la tabla
     */
    private int getPriceFromTable(String position, String ranking, boolean isFavorite) {
        position = position.toUpperCase();
        
        // Determinamos la categoría de ranking
        String rankCategory = getRankingCategory(ranking);
        
        // Buscamos el precio en la tabla
        int price = BASE_PRICE; // Valor predeterminado
        
        switch (position) {
            case "MAIN_EVENT":
                if ("C".equals(rankCategory)) {
                    price = isFavorite ? 75000 : 70000;
                } else if ("1-3".equals(rankCategory)) {
                    price = isFavorite ? 70000 : 65000;
                } else if ("4-5".equals(rankCategory)) {
                    price = isFavorite ? 65000 : 60000;
                } else if ("6-10".equals(rankCategory)) {
                    price = isFavorite ? 63000 : 55000;
                } else if ("11-15".equals(rankCategory)) {
                    price = isFavorite ? 58000 : 50000;
                } else { // Sin ranking
                    price = isFavorite ? 53000 : 45000;
                }
                break;
                
            case "CO_MAIN":
                if ("C".equals(rankCategory)) {
                    price = isFavorite ? 70000 : 65000;
                } else if ("1-3".equals(rankCategory)) {
                    price = isFavorite ? 65000 : 60000;
                } else if ("4-5".equals(rankCategory)) {
                    price = isFavorite ? 60000 : 55000;
                } else if ("6-10".equals(rankCategory)) {
                    price = isFavorite ? 55000 : 50000;
                } else if ("11-15".equals(rankCategory)) {
                    price = isFavorite ? 50000 : 45000;
                } else { // Sin ranking
                    price = isFavorite ? 45000 : 40000;
                }
                break;
                
            case "MAIN_CARD":
                if ("C".equals(rankCategory)) {
                    price = isFavorite ? 65000 : 60000;
                } else if ("1-3".equals(rankCategory)) {
                    price = isFavorite ? 58000 : 53000;
                } else if ("4-5".equals(rankCategory)) {
                    price = isFavorite ? 53000 : 48000;
                } else if ("6-10".equals(rankCategory)) {
                    price = isFavorite ? 48000 : 43000;
                } else if ("11-15".equals(rankCategory)) {
                    price = isFavorite ? 43000 : 38000;
                } else { // Sin ranking
                    price = isFavorite ? 38000 : 33000;
                }
                break;
                
            case "PRELIMS":
                if ("C".equals(rankCategory)) {
                    price = isFavorite ? 55000 : 50000;
                } else if ("1-3".equals(rankCategory)) {
                    price = isFavorite ? 50000 : 45000;
                } else if ("4-5".equals(rankCategory)) {
                    price = isFavorite ? 45000 : 40000;
                } else if ("6-10".equals(rankCategory)) {
                    price = isFavorite ? 40000 : 35000;
                } else if ("11-15".equals(rankCategory)) {
                    price = isFavorite ? 35000 : 30000;
                } else { // Sin ranking
                    price = isFavorite ? 30000 : 25000;
                }
                break;
                
            case "EARLY_PRELIMS":
                if ("C".equals(rankCategory)) {
                    price = isFavorite ? 50000 : 45000;
                } else if ("1-3".equals(rankCategory)) {
                    price = isFavorite ? 45000 : 40000;
                } else if ("4-5".equals(rankCategory)) {
                    price = isFavorite ? 40000 : 35000;
                } else if ("6-10".equals(rankCategory)) {
                    price = isFavorite ? 35000 : 30000;
                } else if ("11-15".equals(rankCategory)) {
                    price = isFavorite ? 30000 : 25000;
                } else { // Sin ranking
                    price = isFavorite ? 25000 : 20000;
                }
                break;
                
            default:
                // Si no reconocemos la posición, usamos PRELIMS como predeterminado
                if ("C".equals(rankCategory)) {
                    price = isFavorite ? 55000 : 50000;
                } else if ("1-3".equals(rankCategory)) {
                    price = isFavorite ? 50000 : 45000;
                } else if ("4-5".equals(rankCategory)) {
                    price = isFavorite ? 45000 : 40000;
                } else if ("6-10".equals(rankCategory)) {
                    price = isFavorite ? 40000 : 35000;
                } else if ("11-15".equals(rankCategory)) {
                    price = isFavorite ? 35000 : 30000;
                } else { // Sin ranking
                    price = isFavorite ? 30000 : 25000;
                }
        }
        
        // Log para debug
        logger.debug("Cálculo de precio para luchador: posición={}, ranking={}, favorito={}, precio final={}",
                position, ranking, isFavorite, price);
        
        return price;
    }
    
    /**
     * Determina la categoría de ranking para buscar en la tabla
     * @param ranking Valor del ranking
     * @return Categoría de ranking ("C", "1-3", "4-5", "6-10", "11-15", o null)
     */
    private String getRankingCategory(String ranking) {
        if (ranking == null || ranking.trim().isEmpty()) {
            return null; // Sin ranking
        }
        
        // Para campeón
        if ("C".equalsIgnoreCase(ranking.trim())) {
            return "C";
        }
        
        // Para rankings numéricos
        try {
            int rankNum = Integer.parseInt(ranking.trim());
            if (rankNum >= 1 && rankNum <= 3) {
                return "1-3";
            } else if (rankNum >= 4 && rankNum <= 5) {
                return "4-5";
            } else if (rankNum >= 6 && rankNum <= 10) {
                return "6-10";
            } else if (rankNum >= 11 && rankNum <= 15) {
                return "11-15";
            }
        } catch (NumberFormatException e) {
            // No es un número válido
            return null;
        }
        
        return null; // Si el ranking no se reconoce
    }
    
    /**
     * Método original para cálculos complejos, mantenido por compatibilidad
     * pero ya no se usa para cálculos de precios.
     */
    public int calculatePrice(
            String position,
            boolean isFavorite,
            double oddsValue,
            String ranking,
            double winLossRatio,
            double finishRate,
            int popularity) {
        
        // En lugar de la lógica compleja, usamos la tabla
        return getPriceFromTable(position, ranking, isFavorite);
    }
}