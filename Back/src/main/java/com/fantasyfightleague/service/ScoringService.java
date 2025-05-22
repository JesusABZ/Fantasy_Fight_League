// Crear: src/main/java/com/fantasyfightleague/service/ScoringService.java
package com.fantasyfightleague.service;

import com.fantasyfightleague.dto.FightResultDTO;
import com.fantasyfightleague.model.Fighter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio para calcular puntuaciones fantasy basadas en estadísticas de combate
 */
@Service
public class ScoringService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScoringService.class);
    
    // Constantes para el sistema de puntuación
    private static final int POINTS_WIN = 20;
    private static final int POINTS_LOSS = 0;
    
    // Bonificaciones por método de victoria
    private static final int BONUS_KO_TKO = 15;
    private static final int BONUS_SUBMISSION = 12;
    private static final int BONUS_DECISION_UNANIMOUS = 5;
    private static final int BONUS_DECISION_MAJORITY = 4;
    private static final int BONUS_DECISION_SPLIT = 3;
    
    // Puntos por estadísticas
    private static final double POINTS_PER_SIGNIFICANT_STRIKE = 0.3;
    private static final int POINTS_PER_TAKEDOWN = 3;
    private static final int POINTS_PER_KNOCKDOWN = 8;
    private static final int POINTS_PER_SUBMISSION_ATTEMPT = 2;
    
    // Bonificaciones por ronda
    private static final int BONUS_ROUND_1 = 10; // Finish en el primer round
    private static final int BONUS_ROUND_2 = 7;  // Finish en el segundo round
    private static final int BONUS_ROUND_3 = 5;  // Finish en el tercer round
    
    /**
     * Calcula los puntos fantasy para un luchador basado en su rendimiento
     * @param result Datos del resultado de la pelea
     * @return Puntos fantasy calculados
     */
    public int calculateFantasyPoints(FightResultDTO result) {
        logger.info("Calculando puntos para {}", result.getNombre());
        
        int totalPoints = 0;
        
        // 1. Puntos base por resultado
        boolean isWinner = "Win".equalsIgnoreCase(result.getResultado());
        totalPoints += isWinner ? POINTS_WIN : POINTS_LOSS;
        
        // 2. Bonificación por método de victoria (solo para ganadores)
        if (isWinner && result.getMetodo() != null) {
            totalPoints += getMethodBonus(result.getMetodo(), result.getTipoDecision(), result.getRound());
        }
        
        // 3. Puntos por estadísticas (para ganadores y perdedores)
        totalPoints += calculateStatisticsPoints(result);
        
        // 4. Bonificaciones especiales
        totalPoints += calculateSpecialBonuses(result);
        
        logger.info("Puntos totales calculados para {}: {}", result.getNombre(), totalPoints);
        
        return Math.max(0, totalPoints); // No permitir puntos negativos
    }
    
    /**
     * Calcula bonificación por método de victoria
     */
    private int getMethodBonus(String metodo, String tipoDecision, Integer round) {
        String metodoBajo = metodo.toLowerCase();
        int bonusMetodo = 0;
        int bonusRonda = 0;
        
        // Bonificación por tipo de victoria
        if (metodoBajo.contains("ko") || metodoBajo.contains("tko") || 
            metodoBajo.contains("knockout") || metodoBajo.contains("technical knockout")) {
            bonusMetodo = BONUS_KO_TKO;
        } else if (metodoBajo.contains("submission") || metodoBajo.contains("sumisión")) {
            bonusMetodo = BONUS_SUBMISSION;
        } else if (metodoBajo.contains("decision") || metodoBajo.contains("decisión")) {
            // Diferenciamos por tipo de decisión
            if (tipoDecision != null) {
                String tipoDecisionBajo = tipoDecision.toLowerCase();
                if (tipoDecisionBajo.contains("unanimous") || tipoDecisionBajo.contains("unánime")) {
                    bonusMetodo = BONUS_DECISION_UNANIMOUS;
                } else if (tipoDecisionBajo.contains("majority") || tipoDecisionBajo.contains("mayoría")) {
                    bonusMetodo = BONUS_DECISION_MAJORITY;
                } else if (tipoDecisionBajo.contains("split") || tipoDecisionBajo.contains("dividida")) {
                    bonusMetodo = BONUS_DECISION_SPLIT;
                } else {
                    bonusMetodo = BONUS_DECISION_UNANIMOUS; // Por defecto, asumimos unánime
                }
            } else {
                bonusMetodo = BONUS_DECISION_UNANIMOUS; // Por defecto si no se especifica
            }
        }
        
        // Bonificación extra por finish temprano (solo para KO/TKO y submissions)
        if (bonusMetodo > BONUS_DECISION_UNANIMOUS && round != null) {
            switch (round) {
                case 1:
                    bonusRonda = BONUS_ROUND_1;
                    break;
                case 2:
                    bonusRonda = BONUS_ROUND_2;
                    break;
                case 3:
                    bonusRonda = BONUS_ROUND_3;
                    break;
                default:
                    bonusRonda = 0;
            }
        }
        
        return bonusMetodo + bonusRonda;
    }
    
    /**
     * Calcula puntos por estadísticas de combate
     */
    private int calculateStatisticsPoints(FightResultDTO result) {
        int statsPoints = 0;
        
        // Golpes significantes
        if (result.getGolpesSignificantes() != null) {
            statsPoints += (int) (result.getGolpesSignificantes() * POINTS_PER_SIGNIFICANT_STRIKE);
        }
        
        // Takedowns exitosos
        if (result.getTakedownsAcertados() != null) {
            statsPoints += result.getTakedownsAcertados() * POINTS_PER_TAKEDOWN;
        }
        
        // Knockdowns
        if (result.getKnockdowns() != null) {
            statsPoints += result.getKnockdowns() * POINTS_PER_KNOCKDOWN;
        }
        
        // Intentos de sumisión
        if (result.getIntentosDeSumision() != null) {
            statsPoints += result.getIntentosDeSumision() * POINTS_PER_SUBMISSION_ATTEMPT;
        }
        
        return statsPoints;
    }
    
    /**
     * Calcula bonificaciones especiales
     */
    private int calculateSpecialBonuses(FightResultDTO result) {
        int bonusPoints = 0;
        
        // Bonificación por alta precisión (si tiene más del 70% de precisión en golpes)
        if (result.getGolpesTotales() != null && result.getGolpesAcertados() != null && 
            result.getGolpesTotales() > 0) {
            double precision = (double) result.getGolpesAcertados() / result.getGolpesTotales();
            if (precision >= 0.7 && result.getGolpesAcertados() >= 20) {
                bonusPoints += 5; // Bonificación por alta precisión
            }
        }
        
        // Bonificación por alto volumen de takedowns exitosos
        if (result.getTakedownsAcertados() != null && result.getTakedownsAcertados() >= 5) {
            bonusPoints += 5; // Bonificación por dominio en el grappling
        }
        
        // Bonificación por múltiples knockdowns
        if (result.getKnockdowns() != null && result.getKnockdowns() >= 2) {
            bonusPoints += 10; // Bonificación por dominio en el striking
        }
        
        return bonusPoints;
    }
    
    /**
     * Genera un resumen detallado de cómo se calcularon los puntos
     */
    public String generatePointsBreakdown(FightResultDTO result, int totalPoints) {
        StringBuilder breakdown = new StringBuilder();
        breakdown.append("Desglose de puntos para ").append(result.getNombre()).append(":\n");
        
        boolean isWinner = "Win".equalsIgnoreCase(result.getResultado());
        breakdown.append("- Resultado: ").append(isWinner ? "Victoria (+20)" : "Derrota (+0)").append("\n");
        
        if (isWinner && result.getMetodo() != null) {
            int methodBonus = getMethodBonus(result.getMetodo(), result.getTipoDecision(), result.getRound());
            breakdown.append("- Método de victoria: ").append(result.getMetodo());
            if (result.getTipoDecision() != null) {
                breakdown.append(" (").append(result.getTipoDecision()).append(")");
            }
            breakdown.append(" (+").append(methodBonus).append(")\n");
        }
        
        int statsPoints = calculateStatisticsPoints(result);
        breakdown.append("- Estadísticas de combate: (+").append(statsPoints).append(")\n");
        
        int bonusPoints = calculateSpecialBonuses(result);
        if (bonusPoints > 0) {
            breakdown.append("- Bonificaciones especiales: (+").append(bonusPoints).append(")\n");
        }
        
        breakdown.append("TOTAL: ").append(totalPoints).append(" puntos");
        
        return breakdown.toString();
    }
}