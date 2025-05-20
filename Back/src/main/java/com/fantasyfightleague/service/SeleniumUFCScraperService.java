// Modificación a SeleniumUFCScraperService.java
package com.fantasyfightleague.service;

import com.fantasyfightleague.model.Fighter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class SeleniumUFCScraperService {

    private static final Logger logger = LoggerFactory.getLogger(SeleniumUFCScraperService.class);

    /**
     * Extrae los peleadores del evento UFC usando Selenium WebDriver
     *
     * @param eventUrl URL del evento UFC
     * @return Lista de peleadores extraídos
     */
    public List<Fighter> extractFightersFromUFC(String eventUrl) {
        List<Fighter> fighters = new ArrayList<>();
        WebDriver driver = null;

        try {
            logger.info("Iniciando extracción con Selenium desde {}", eventUrl);
            
            // Configurar WebDriver
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
            
            driver = new ChromeDriver(options);
            driver.get(eventUrl);
            
            // Intentar cerrar el banner de cookies
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("#onetrust-accept-btn-handler")));
                acceptButton.click();
                logger.info("Banner de cookies cerrado");
            } catch (Exception e) {
                logger.warn("No se pudo cerrar el banner de cookies: {}", e.getMessage());
            }
            
            // Esperar a que se carguen los elementos de la página
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".c-listing-fight")));
            
            // Extraer todos los bloques de peleas
            List<WebElement> fightBlocks = driver.findElements(By.cssSelector(".c-listing-fight"));
            logger.info("1. Bloques encontrados: {}", fightBlocks.size());
            
            // Recolectar los IDs de todas las peleas
            List<String> fightIds = new ArrayList<>();
            
            for (WebElement fightBlock : fightBlocks) {
                try {
                    String fightId = fightBlock.getAttribute("data-fmid");
                    if (fightId != null && !fightId.isEmpty()) {
                        fightIds.add(fightId);
                        logger.info("ID de pelea encontrado: {}", fightId);
                    }
                } catch (Exception e) {
                    logger.warn("Error extrayendo ID de pelea: {}", e.getMessage());
                }
            }
            
            logger.info("2. IDs de los bloques: {}", String.join(", ", fightIds));
            
            // Para cada ID de pelea, visitar la URL específica
            int blockCounter = 1;
            for (String fightId : fightIds) {
                String fightUrl = eventUrl + "#" + fightId;
                logger.info("3. Entrando a bloque {}", blockCounter);
                logger.info("Visitando URL de pelea: {}", fightUrl);
                
                WebDriver fightDriver = null;
                try {
                    // Usar un driver separado para cada pelea para evitar problemas de navegación
                    fightDriver = new ChromeDriver(options);
                    fightDriver.get(fightUrl);
                    
                    // Esperar a que cargue los detalles de la pelea
                    WebDriverWait fightWait = new WebDriverWait(fightDriver, Duration.ofSeconds(10));
                    fightWait.until(ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(".c-fight-card, .c-card-event--fight-card")));
                    
                    // Extraer información de los luchadores
                    List<WebElement> fighterElements = fightDriver.findElements(
                            By.cssSelector(".c-fight-card--athlete, .c-card-event--athlete-panel"));
                    
                    if (fighterElements.size() >= 2) {
                        // Primer peleador
                        WebElement fighter1Element = fighterElements.get(0);
                        Fighter fighter1 = extractFighterInfo(fighter1Element, fightDriver, "4. Datos de peleador/a uno");
                        if (fighter1 != null) {
                            fighters.add(fighter1);
                        }
                        
                        // Segundo peleador
                        WebElement fighter2Element = fighterElements.get(1);
                        Fighter fighter2 = extractFighterInfo(fighter2Element, fightDriver, "5. Datos de peleador/a dos");
                        if (fighter2 != null) {
                            fighters.add(fighter2);
                        }
                    } else {
                        logger.warn("No se encontraron suficientes elementos de peleadores en la pelea {}", fightId);
                    }
                    
                } catch (Exception e) {
                    logger.error("Error procesando pelea {}: {}", fightId, e.getMessage());
                } finally {
                    if (fightDriver != null) {
                        fightDriver.quit();
                    }
                    blockCounter++;
                }
            }
            
            logger.info("Extracción con Selenium completada. Se encontraron {} luchadores", fighters.size());
            
        } catch (Exception e) {
            logger.error("Error durante la extracción con Selenium: {}", e.getMessage(), e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        
        return fighters;
    }
    
    /**
     * Extrae la información de un peleador desde su elemento HTML
     */
    private Fighter extractFighterInfo(WebElement fighterElement, WebDriver driver, String logPrefix) {
        try {
            Fighter fighter = new Fighter();
            
            // Extraer nombre
            WebElement nameElement = fighterElement.findElement(
                By.cssSelector(".c-fight-card--athlete-name, .c-card-event--athlete-name"));
            String name = nameElement.getText().trim();
            fighter.setName(name);
            
            // Extraer récord
            try {
                WebElement recordElement = fighterElement.findElement(
                    By.cssSelector(".c-record__promoted, .c-card-event--record"));
                String record = recordElement.getText().trim();
                fighter.setRecord(record);
                logger.info("{} - Récord: {}", logPrefix, record);
            } catch (Exception e) {
                String record = generateRealisticRecord(null);
                fighter.setRecord(record);
                logger.info("{} - Récord generado: {}", logPrefix, record);
            }
            
            // Extraer nacionalidad
            try {
                WebElement countryElement = fighterElement.findElement(
                    By.cssSelector(".c-fight-card--athlete-country, .c-card-event--country"));
                String nationality = countryElement.getText().trim();
                fighter.setNationality(nationality);
                logger.info("{} - Nacionalidad: {}", logPrefix, nationality);
            } catch (Exception e) {
                fighter.setNationality("España");
                logger.info("{} - Nacionalidad por defecto: España", logPrefix);
            }
            
            // Extraer categoría de peso
            try {
                List<WebElement> weightClassElements = driver.findElements(
                    By.cssSelector(".c-fight-card--class, .c-listing-fight__class"));
                if (!weightClassElements.isEmpty()) {
                    String weightClass = weightClassElements.get(0).getText().trim();
                    String mappedClass = mapWeightClass(weightClass);
                    fighter.setWeightClass(mappedClass);
                    logger.info("{} - Categoría de peso: {}", logPrefix, mappedClass);
                } else {
                    fighter.setWeightClass("Peso Wélter"); // Valor por defecto
                    logger.info("{} - Categoría de peso por defecto: Peso Wélter", logPrefix);
                }
            } catch (Exception e) {
                fighter.setWeightClass("Peso Wélter"); // Valor por defecto
                logger.info("{} - Categoría de peso por defecto: Peso Wélter", logPrefix);
            }
            
            // Extraer altura
            try {
                List<WebElement> heightElements = driver.findElements(
                    By.cssSelector("[data-label='ALTO'] .c-stat-compare--number, .c-stat-3col__number:has(+ .c-stat-3col__label:contains('Alto'))"));
                if (!heightElements.isEmpty()) {
                    String heightText = heightElements.get(0).getText().trim();
                    parseHeight(heightText, fighter);
                    logger.info("{} - Altura: {} m", logPrefix, fighter.getHeight());
                } else {
                    double height = getDefaultHeightForClass(fighter.getWeightClass());
                    fighter.setHeight(height);
                    logger.info("{} - Altura por defecto: {} m", logPrefix, height);
                }
            } catch (Exception e) {
                double height = getDefaultHeightForClass(fighter.getWeightClass());
                fighter.setHeight(height);
                logger.info("{} - Altura por defecto: {} m", logPrefix, height);
            }
            
            // Extraer peso
            try {
                List<WebElement> weightElements = driver.findElements(
                    By.cssSelector("[data-label='PESO'] .c-stat-compare--number, .c-stat-3col__number:has(+ .c-stat-3col__label:contains('Peso'))"));
                if (!weightElements.isEmpty()) {
                    String weightText = weightElements.get(0).getText().trim();
                    parseWeight(weightText, fighter);
                    logger.info("{} - Peso: {} kg", logPrefix, fighter.getWeight());
                } else {
                    double weight = getDefaultWeightForClass(fighter.getWeightClass());
                    fighter.setWeight(weight);
                    logger.info("{} - Peso por defecto: {} kg", logPrefix, weight);
                }
            } catch (Exception e) {
                double weight = getDefaultWeightForClass(fighter.getWeightClass());
                fighter.setWeight(weight);
                logger.info("{} - Peso por defecto: {} kg", logPrefix, weight);
            }
            
            // Establecer otros valores predeterminados
            fighter.setAge(30);
            fighter.setBasePrice(60);
            fighter.setPrice(60);
            fighter.setActive(true);
            
            logger.info("{} - Nombre: {}", logPrefix, name);
            logger.info("{} - Edad: 30 (por defecto)", logPrefix);
            logger.info("{} - Precio: 60 (por defecto)", logPrefix);
            
            return fighter;
            
        } catch (Exception e) {
            logger.error("Error extrayendo información del luchador: {}", e.getMessage(), e);
            return null;
        }
    }

    // Método para analizar altura (convertir de texto a valor numérico)
    private void parseHeight(String heightText, Fighter fighter) {
        try {
            if (heightText.contains("'")) {
                // Formato pies/pulgadas (5' 10")
                String[] parts = heightText.split("'");
                int feet = Integer.parseInt(parts[0].trim());
                int inches = 0;
                if (parts.length > 1) {
                    inches = Integer.parseInt(parts[1].replaceAll("[^0-9]", "").trim());
                }
                double heightInMeters = (feet * 30.48 + inches * 2.54) / 100; // Convertir a metros
                fighter.setHeight(heightInMeters);
            } else if (heightText.contains("cm")) {
                // Formato centímetros
                String cmPart = heightText.replaceAll("[^0-9.]", "");
                if (!cmPart.isEmpty()) {
                    double heightInCm = Double.parseDouble(cmPart);
                    fighter.setHeight(heightInCm / 100); // Convertir a metros
                }
            } else {
                // Intentar extraer solo números
                String numericPart = heightText.replaceAll("[^0-9.]", "");
                if (!numericPart.isEmpty()) {
                    double value = Double.parseDouble(numericPart);
                    // Si el valor es mayor a 3, probablemente sea cm
                    if (value > 3) {
                        fighter.setHeight(value / 100);
                    } else {
                        fighter.setHeight(value);
                    }
                } else {
                    fighter.setHeight(getDefaultHeightForClass(fighter.getWeightClass()));
                }
            }
        } catch (Exception e) {
            fighter.setHeight(getDefaultHeightForClass(fighter.getWeightClass()));
            logger.warn("Error parseando altura '{}': {}", heightText, e.getMessage());
        }
    }

    // Método para analizar peso (convertir de texto a valor numérico)
    private void parseWeight(String weightText, Fighter fighter) {
        try {
            if (weightText.contains("lbs") || weightText.contains("LB")) {
                // Libras -> kg
                String lbsPart = weightText.replaceAll("[^0-9.]", "");
                if (!lbsPart.isEmpty()) {
                    double weightInLbs = Double.parseDouble(lbsPart);
                    fighter.setWeight(weightInLbs * 0.453592); // Convertir a kg
                }
            } else if (weightText.contains("kg")) {
                // Ya está en kg
                String kgPart = weightText.replaceAll("[^0-9.]", "");
                if (!kgPart.isEmpty()) {
                    fighter.setWeight(Double.parseDouble(kgPart));
                }
            } else {
                // Intentar extraer solo números
                String numericPart = weightText.replaceAll("[^0-9.]", "");
                if (!numericPart.isEmpty()) {
                    double value = Double.parseDouble(numericPart);
                    // Si el valor es mayor a 100, probablemente sea libras
                    if (value > 100) {
                        fighter.setWeight(value * 0.453592);
                    } else {
                        fighter.setWeight(value);
                    }
                } else {
                    fighter.setWeight(getDefaultWeightForClass(fighter.getWeightClass()));
                }
            }
        } catch (Exception e) {
            fighter.setWeight(getDefaultWeightForClass(fighter.getWeightClass()));
            logger.warn("Error parseando peso '{}': {}", weightText, e.getMessage());
        }
    }

    // Generar récord realista para un peleador
    private String generateRealisticRecord(Fighter fighter) {
        Random random = new Random();
        int wins = 5 + random.nextInt(15); // Entre 5 y 19 victorias
        int losses = 1 + random.nextInt(5); // Entre 1 y 5 derrotas
        int draws = random.nextInt(2); // 0 o 1 empates
        
        if (draws > 0) {
            return wins + "-" + losses + "-" + draws;
        } else {
            return wins + "-" + losses;
        }
    }
    
    // Obtener altura predeterminada para una categoría de peso
    private double getDefaultHeightForClass(String weightClass) {
        if (weightClass == null) return 1.80;
        
        weightClass = weightClass.toLowerCase();
        
        if (weightClass.contains("completo")) return 1.93;
        if (weightClass.contains("semicompleto")) return 1.88;
        if (weightClass.contains("mediano")) return 1.83;
        if (weightClass.contains("wélter")) return 1.80;
        if (weightClass.contains("ligero")) return 1.75;
        if (weightClass.contains("pluma") && !weightClass.contains("femenino")) return 1.73;
        if (weightClass.contains("gallo") && !weightClass.contains("femenino")) return 1.70;
        if (weightClass.contains("mosca") && !weightClass.contains("femenino")) return 1.65;
        if (weightClass.contains("paja")) return 1.63;
        if (weightClass.contains("mosca") && weightClass.contains("femenino")) return 1.63;
        if (weightClass.contains("gallo") && weightClass.contains("femenino")) return 1.68;
        if (weightClass.contains("pluma") && weightClass.contains("femenino")) return 1.70;
        
        return 1.80; // Altura predeterminada
    }
    
    // Obtener peso predeterminado para una categoría de peso
    private double getDefaultWeightForClass(String weightClass) {
        if (weightClass == null) return 77.0;
        
        weightClass = weightClass.toLowerCase();
        
        if (weightClass.contains("completo")) return 120.0;
        if (weightClass.contains("semicompleto")) return 93.0;
        if (weightClass.contains("mediano")) return 84.0;
        if (weightClass.contains("wélter")) return 77.0;
        if (weightClass.contains("ligero")) return 70.0;
        if (weightClass.contains("pluma")) {
            if (weightClass.contains("femenino")) return 66.0;
            return 66.0;
        }
        if (weightClass.contains("gallo")) {
            if (weightClass.contains("femenino")) return 61.0;
            return 61.0;
        }
        if (weightClass.contains("mosca")) {
            if (weightClass.contains("femenino")) return 52.0;
            return 57.0;
        }
        if (weightClass.contains("paja")) return 52.0;
        
        return 77.0; // Peso Wélter como valor por defecto
    }
    
    // Normaliza nombres de categorías de peso a formato estándar de UFC
    private String mapWeightClass(String apiWeightClass) {
        if (apiWeightClass == null || apiWeightClass.trim().isEmpty()) {
            return "Peso Wélter"; // Valor por defecto
        }
        
        apiWeightClass = apiWeightClass.toLowerCase().trim();
        
        if (apiWeightClass.contains("mosca") && (apiWeightClass.contains("mujeres") || apiWeightClass.contains("femenino") || 
            apiWeightClass.contains("women") || apiWeightClass.contains("mujer"))) {
            return "Peso Mosca (femenino)";
        } else if (apiWeightClass.contains("gallo") && (apiWeightClass.contains("mujeres") || apiWeightClass.contains("femenino") || 
                   apiWeightClass.contains("women") || apiWeightClass.contains("mujer"))) {
            return "Peso Gallo (femenino)";
        } else if (apiWeightClass.contains("pluma") && (apiWeightClass.contains("mujeres") || apiWeightClass.contains("femenino") || 
                   apiWeightClass.contains("women") || apiWeightClass.contains("mujer"))) {
            return "Peso Pluma (femenino)";
        } else if (apiWeightClass.contains("paja") || apiWeightClass.contains("strawweight")) {
            return "Peso Paja (femenino)";
        } else if (apiWeightClass.contains("mosca") || apiWeightClass.contains("flyweight")) {
            return "Peso Mosca";
        } else if (apiWeightClass.contains("gallo") || apiWeightClass.contains("bantamweight")) {
            return "Peso Gallo";
        } else if (apiWeightClass.contains("pluma") || apiWeightClass.contains("featherweight")) {
            return "Peso Pluma";
        } else if (apiWeightClass.contains("ligero") || apiWeightClass.contains("lightweight")) {
            return "Peso Ligero";
        } else if (apiWeightClass.contains("welter") || apiWeightClass.contains("wélter")) {
            return "Peso Wélter";
        } else if (apiWeightClass.contains("median") || apiWeightClass.contains("middleweight")) {
            return "Peso Mediano";
        } else if (apiWeightClass.contains("semipes") || apiWeightClass.contains("semicompleto") || 
                  apiWeightClass.contains("light heavy")) {
            return "Peso Semicompleto";
        } else if ((apiWeightClass.contains("pesado") || apiWeightClass.contains("heavyweight")) && 
                  !apiWeightClass.contains("light")) {
            return "Peso Completo";
        }
        
        return "Peso Wélter"; // Valor por defecto si no se puede determinar
    }
}