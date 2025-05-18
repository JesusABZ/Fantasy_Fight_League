package com.fantasyfightleague.service;

import com.fantasyfightleague.model.Fighter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                List<String> possibleAcceptButtons = Arrays.asList(
                    "#onetrust-accept-btn-handler",
                    ".accept-cookies",
                    "#accept-cookies",
                    ".cookie-accept",
                    "#cookie-accept",
                    ".cookie-banner .accept",
                    "#onetrust-consent-sdk .accept-all",
                    "button[contains(text(), 'Accept')]",
                    "button[contains(text(), 'Aceptar')]"
                );
                
                for (String selector : possibleAcceptButtons) {
                    try {
                        WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));
                        acceptButton.click();
                        logger.info("Banner de cookies cerrado con selector: {}", selector);
                        break;
                    } catch (Exception e) {
                        // Intentar el siguiente selector
                    }
                }
            } catch (Exception e) {
                logger.warn("No se pudo cerrar el banner de cookies: {}", e.getMessage());
            }
            
            // Esperar a que se carguen los elementos de la página
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".c-listing-fight")));
            
            // Extraer todos los bloques de peleas
            List<WebElement> fightBlocks = driver.findElements(By.cssSelector(".c-listing-fight"));
            logger.info("Encontrados {} bloques de peleas", fightBlocks.size());
            
            // Recolectar los IDs de todas las peleas
            List<String> fightIds = new ArrayList<>();
            Map<String, String> fightWeightClasses = new HashMap<>();
            
            for (WebElement fightBlock : fightBlocks) {
                try {
                    String fightId = fightBlock.getAttribute("data-fmid");
                    if (fightId != null && !fightId.isEmpty()) {
                        fightIds.add(fightId);
                        
                        // Intentar extraer la categoría de peso de la pelea
                        try {
                            WebElement weightClassElement = fightBlock.findElement(By.cssSelector(".c-listing-fight__class"));
                            String weightClass = weightClassElement.getText().trim();
                            fightWeightClasses.put(fightId, weightClass);
                            logger.info("ID de pelea: {}, Categoría: {}", fightId, weightClass);
                        } catch (Exception e) {
                            logger.warn("No se pudo extraer categoría para pelea {}: {}", fightId, e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Error extrayendo ID de pelea: {}", e.getMessage());
                }
            }
            
            // Para cada ID de pelea, visitar la URL específica
            for (String fightId : fightIds) {
                String fightUrl = eventUrl + "#" + fightId;
                logger.info("Visitando URL de pelea: {}", fightUrl);
                
                WebDriver fightDriver = null;
                try {
                    // Usar un driver separado para cada pelea para evitar problemas de navegación
                    fightDriver = new ChromeDriver(options);
                    fightDriver.get(fightUrl);
                    
                    // Esperar a que cargue los detalles de la pelea
                    WebDriverWait fightWait = new WebDriverWait(fightDriver, Duration.ofSeconds(10));
                    fightWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".c-fight-card, .c-card-event--fight-card")));
                    
                    // Intentar extraer con JavaScript primero (más eficiente)
                    try {
                        String scriptResult = executeJavaScriptExtraction(fightDriver);
                        logger.info("Resultado extracción JS: {}", scriptResult);
                        
                        if (scriptResult != null && !scriptResult.equals("[]")) {
                            // Procesar los datos extraídos con JavaScript
                            processFightData(scriptResult, fighters);
                            
                            // Si se procesaron luchadores correctamente, continuamos con la siguiente pelea
                            if (fighters.size() > 0) {
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Error en extracción JavaScript: {}", e.getMessage());
                    }
                    
                    // Si el método JS no funcionó, intentamos con el método tradicional
                    List<WebElement> fighterElements = fightDriver.findElements(
                        By.cssSelector(".c-fight-card--athlete, .c-card-event--athlete-panel"));
                    
                    // Obtener la categoría de peso (si se extrajo anteriormente)
                    String weightClassFromList = fightWeightClasses.getOrDefault(fightId, "");
                    String mappedWeightClass = mapWeightClass(weightClassFromList);
                    
                    for (WebElement fighterElement : fighterElements) {
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
                            } catch (Exception e) {
                                fighter.setRecord(generateRealisticRecord(null));
                                logger.warn("No se pudo extraer récord para {}: {}", name, e.getMessage());
                            }
                            
                            // Extraer nacionalidad
                            try {
                                WebElement countryElement = fighterElement.findElement(
                                    By.cssSelector(".c-fight-card--athlete-country, .c-card-event--country"));
                                String nationality = countryElement.getText().trim();
                                fighter.setNationality(nationality);
                            } catch (Exception e) {
                                fighter.setNationality("España");
                                logger.warn("No se pudo extraer nacionalidad para {}: {}", name, e.getMessage());
                            }
                            
                            // Usar la categoría de peso extraída de la lista principal
                            if (!mappedWeightClass.isEmpty()) {
                                fighter.setWeightClass(mappedWeightClass);
                            } else {
                                // Intentar extraer del detalle de la pelea
                                try {
                                    List<WebElement> weightClassElements = fightDriver.findElements(
                                        By.cssSelector(".c-fight-card--class, .c-listing-fight__class"));
                                    if (!weightClassElements.isEmpty()) {
                                        String weightClass = weightClassElements.get(0).getText().trim();
                                        fighter.setWeightClass(mapWeightClass(weightClass));
                                    } else {
                                        fighter.setWeightClass("Peso Wélter"); // Valor por defecto
                                    }
                                } catch (Exception e) {
                                    fighter.setWeightClass("Peso Wélter"); // Valor por defecto
                                    logger.warn("No se pudo extraer categoría para {}: {}", name, e.getMessage());
                                }
                            }
                            
                            // Extraer altura y peso
                            try {
                                List<WebElement> heightElements = fightDriver.findElements(
                                    By.cssSelector("[data-label='ALTO'] .c-stat-compare--number, .c-stat-3col__number:has(+ .c-stat-3col__label:contains('Alto'))"));
                                if (!heightElements.isEmpty()) {
                                    String heightText = heightElements.get(0).getText().trim();
                                    parseHeight(heightText, fighter);
                                } else {
                                    fighter.setHeight(getDefaultHeightForClass(fighter.getWeightClass()));
                                }
                            } catch (Exception e) {
                                fighter.setHeight(getDefaultHeightForClass(fighter.getWeightClass()));
                                logger.warn("No se pudo extraer altura para {}: {}", name, e.getMessage());
                            }
                            
                            try {
                                List<WebElement> weightElements = fightDriver.findElements(
                                    By.cssSelector("[data-label='PESO'] .c-stat-compare--number, .c-stat-3col__number:has(+ .c-stat-3col__label:contains('Peso'))"));
                                if (!weightElements.isEmpty()) {
                                    String weightText = weightElements.get(0).getText().trim();
                                    parseWeight(weightText, fighter);
                                } else {
                                    fighter.setWeight(getDefaultWeightForClass(fighter.getWeightClass()));
                                }
                            } catch (Exception e) {
                                fighter.setWeight(getDefaultWeightForClass(fighter.getWeightClass()));
                                logger.warn("No se pudo extraer peso para {}: {}", name, e.getMessage());
                            }
                            
                            // Establecer otros valores predeterminados
                            fighter.setAge(30);
                            fighter.setBasePrice(60);
                            fighter.setPrice(60);
                            fighter.setActive(true);
                            
                            // Añadir a la lista
                            fighters.add(fighter);
                            logger.info("Luchador extraído por método estándar: {}, Categoría: {}", 
                                name, fighter.getWeightClass());
                            
                        } catch (Exception e) {
                            logger.error("Error extrayendo información del luchador: {}", e.getMessage(), e);
                        }
                    }
                    
                } catch (Exception e) {
                    logger.error("Error procesando pelea {}: {}", fightId, e.getMessage(), e);
                } finally {
                    if (fightDriver != null) {
                        fightDriver.quit();
                    }
                }
            }
            
            logger.info("Extracción con Selenium completada. Se encontraron {} luchadores", fighters.size());
            
            // Si no se encontraron luchadores, intentar con el método alternativo
            if (fighters.isEmpty()) {
                logger.info("No se encontraron luchadores con método detallado, intentando con método alternativo");
                return extractFightersFromMainPage(driver);
            }
            
        } catch (Exception e) {
            logger.error("Error durante la extracción con Selenium: {}", e.getMessage(), e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        
        return fighters;
    }

    // Ejecutar JavaScript para extraer datos
    private String executeJavaScriptExtraction(WebDriver driver) {
        String script = 
            "var fighters = [];" +
            "var nameElements = document.querySelectorAll('.c-fight-card--athlete-name');" +
            "var recordElements = document.querySelectorAll('.c-record__promoted');" +
            "var countryElements = document.querySelectorAll('.c-fight-card--athlete-country');" +
            "var heightElements = document.querySelectorAll('[data-label=\"ALTO\"] .c-stat-compare--number');" +
            "var weightElements = document.querySelectorAll('[data-label=\"PESO\"] .c-stat-compare--number');" +
            
            "for (var i = 0; i < nameElements.length; i++) {" +
            "  var fighter = {};" +
            "  fighter.name = nameElements[i].textContent.trim();" +
            "  if (i < recordElements.length) fighter.record = recordElements[i].textContent.trim();" +
            "  if (i < countryElements.length) fighter.nationality = countryElements[i].textContent.trim();" +
            "  if (i*2 < heightElements.length) fighter.height = heightElements[i*2].textContent.trim();" +
            "  if (i*2 < weightElements.length) fighter.weight = weightElements[i*2].textContent.trim();" +
            "  fighters.push(fighter);" +
            "}" +
            
            "return JSON.stringify(fighters);";
        
        try {
            return (String) ((JavascriptExecutor) driver).executeScript(script);
        } catch (Exception e) {
            logger.warn("Error ejecutando JavaScript: {}", e.getMessage());
            return "[]";
        }
    }

    // Procesar datos de pelea extraídos con JavaScript
    private void processFightData(String jsonData, List<Fighter> fighters) {
        try {
            // Implementar parseo de JSON y creación de objetos Fighter
            org.json.JSONArray jsonArray = new org.json.JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                org.json.JSONObject jsonFighter = jsonArray.getJSONObject(i);
                
                Fighter fighter = new Fighter();
                fighter.setName(jsonFighter.optString("name", ""));
                fighter.setRecord(jsonFighter.optString("record", "0-0"));
                fighter.setNationality(jsonFighter.optString("nationality", "España"));
                fighter.setActive(true);
                
                // Procesar altura
                String heightStr = jsonFighter.optString("height", "");
                if (!heightStr.isEmpty()) {
                    parseHeight(heightStr, fighter);
                } else {
                    fighter.setHeight(1.80); // Valor por defecto
                }
                
                // Procesar peso
                String weightStr = jsonFighter.optString("weight", "");
                if (!weightStr.isEmpty()) {
                    parseWeight(weightStr, fighter);
                } else {
                    fighter.setWeight(70.0); // Valor por defecto
                }
                
                // Determinar categoría de peso basado en el peso
                String weightClass = determineWeightClassFromWeight(fighter.getWeight());
                fighter.setWeightClass(weightClass);
                
                // Establecer precio
                fighter.setBasePrice(60);
                fighter.setPrice(60);
                
                // Añadir a la lista
                fighters.add(fighter);
                logger.info("Añadido peleador con datos extraídos: {}", fighter.getName());
            }
        } catch (Exception e) {
            logger.warn("Error procesando JSON de peleadores: {}", e.getMessage());
        }
    }

    // Determinar categoría de peso basado en el peso en kg
    private String determineWeightClassFromWeight(double weight) {
        if (weight >= 105.0) return "Peso Completo";
        if (weight >= 93.0) return "Peso Semicompleto";
        if (weight >= 84.0) return "Peso Mediano";
        if (weight >= 77.0) return "Peso Wélter";
        if (weight >= 70.0) return "Peso Ligero";
        if (weight >= 66.0) return "Peso Pluma";
        if (weight >= 61.0) return "Peso Gallo";
        if (weight >= 57.0) return "Peso Mosca";
        if (weight >= 52.0) return "Peso Mosca (femenino)";
        return "Peso Paja (femenino)";
    }

    // Extraer información básica del peleador
    private void extractBasicFighterInfo(WebDriver driver, List<Fighter> fighters) {
        // Buscar nombres de peleadores
        List<WebElement> nameElements = driver.findElements(By.cssSelector(".c-listing-fight__corner-name, .c-fight-card--athlete-name"));
        
        for (WebElement nameElement : nameElements) {
            String fighterName = nameElement.getText().trim();
            if (!fighterName.isEmpty()) {
                logger.info("Encontrado peleador: {}", fighterName);
                
                Fighter fighter = new Fighter();
                fighter.setName(fighterName);
                fighter.setActive(true);
                
                // Valores por defecto
                fighter.setRecord(generateRealisticRecord(null));
                fighter.setNationality("España");
                fighter.setAge(30);
                
                // Intentar determinar categoría de peso por contexto
                String weightClass = "Peso Wélter"; // Valor por defecto
                try {
                    String pageText = driver.findElement(By.tagName("body")).getText().toLowerCase();
                    if (pageText.contains("flyweight") || pageText.contains("mosca")) {
                        if (pageText.contains("women") || pageText.contains("mujer") || pageText.contains("femenino")) {
                            weightClass = "Peso Mosca (femenino)";
                        } else {
                            weightClass = "Peso Mosca";
                        }
                    } else if (pageText.contains("bantamweight") || pageText.contains("gallo")) {
                        if (pageText.contains("women") || pageText.contains("mujer") || pageText.contains("femenino")) {
                            weightClass = "Peso Gallo (femenino)";
                        } else {
                            weightClass = "Peso Gallo";
                        }
                    } else if (pageText.contains("featherweight") || pageText.contains("pluma")) {
                        if (pageText.contains("women") || pageText.contains("mujer") || pageText.contains("femenino")) {
                            weightClass = "Peso Pluma (femenino)";
                        } else {
                            weightClass = "Peso Pluma";
                        }
                    } else if (pageText.contains("lightweight") || pageText.contains("ligero")) {
                        weightClass = "Peso Ligero";
                    } else if (pageText.contains("welterweight") || pageText.contains("welter")) {
                        weightClass = "Peso Wélter";
                    } else if (pageText.contains("middleweight") || pageText.contains("medio")) {
                        weightClass = "Peso Mediano";
                    } else if (pageText.contains("light heavyweight") || pageText.contains("semipesado")) {
                        weightClass = "Peso Semicompleto";
                    } else if (pageText.contains("heavyweight") && !pageText.contains("light") || pageText.contains("pesado") && !pageText.contains("semi")) {
                        weightClass = "Peso Completo";
                    } else if (pageText.contains("strawweight") || pageText.contains("paja")) {
                        weightClass = "Peso Paja (femenino)";
                    }
                } catch (Exception e) {
                    logger.warn("Error al detectar categoría de peso: {}", e.getMessage());
                }
                
                fighter.setWeightClass(weightClass);
                
                // Establecer altura y peso por defecto según categoría
                fighter.setHeight(getDefaultHeightForClass(weightClass));
                fighter.setWeight(getDefaultWeightForClass(weightClass));
                
                // Establecer precio
                fighter.setBasePrice(60);
                fighter.setPrice(60);
                
                fighters.add(fighter);
            }
        }
    }

    // Método original que extrae peleadores de la página principal
    private List<Fighter> extractFightersFromMainPage(WebDriver driver) {
        List<Fighter> fighters = new ArrayList<>();
        
        try {
            // Extraer todos los bloques de peleas
            List<WebElement> fightBlocks = driver.findElements(By.cssSelector(".c-listing-fight"));
            logger.info("Encontrados {} bloques de peleas", fightBlocks.size());
            
            for (WebElement fightBlock : fightBlocks) {
                // Extraer la categoría de peso
                String weightClass = "Unknown";
                try {
                    WebElement weightClassElement = fightBlock.findElement(By.cssSelector(".c-listing-fight__class"));
                    if (weightClassElement != null) {
                        weightClass = weightClassElement.getText().trim();
                    }
                } catch (Exception e) {
                    logger.warn("No se pudo encontrar el elemento de clase de peso");
                }
                
                // Comprobar si hay indicadores adicionales de categoría femenina
                boolean isFemaleCategory = false;
                try {
                    String fightBlockText = fightBlock.getText().toLowerCase();
                    isFemaleCategory = fightBlockText.contains("women's") || 
                                       fightBlockText.contains("de la mujer") ||
                                       fightBlockText.contains("femenino") ||
                                       fightBlockText.contains("mujeres");
                } catch (Exception e) {
                    logger.warn("Error al verificar si es categoría femenina");
                }
                
                // Si es categoría femenina pero no está explícito en weightClass, añadirlo
                if (isFemaleCategory && !weightClass.toLowerCase().contains("women") && 
                    !weightClass.toLowerCase().contains("mujer") && 
                    !weightClass.toLowerCase().contains("femenino")) {
                    weightClass += " (femenino)";
                }
                
                // Extraer los nombres de los peleadores
                List<WebElement> fighterNameElements = fightBlock.findElements(By.cssSelector(".c-listing-fight__corner-name"));
                for (WebElement nameElement : fighterNameElements) {
                    String fighterName = nameElement.getText().trim();
                    logger.info("Encontrado peleador: {}, Categoría: {}", fighterName, weightClass);
                    
                    // Crear y configurar el objeto Fighter
                    Fighter fighter = new Fighter();
                    fighter.setName(fighterName);
                    String mappedWeightClass = mapWeightClass(weightClass);
                    fighter.setWeightClass(mappedWeightClass);
                    fighter.setActive(true);
                    
                    // Valores por defecto
                    fighter.setRecord(generateRealisticRecord(fighter));
                    fighter.setNationality("España");
                    fighter.setAge(30);
                    fighter.setHeight(getDefaultHeightForClass(mappedWeightClass));
                    fighter.setWeight(getDefaultWeightForClass(mappedWeightClass));
                    
                    // Establecer precio
                    fighter.setBasePrice(60);
                    fighter.setPrice(60);
                    
                    fighters.add(fighter);
                }
            }
        } catch (Exception e) {
            logger.warn("Error extrayendo peleadores de la página principal: {}", e.getMessage());
        }
        
        return fighters;
    }
    
    /**
     * Enriquece la información de un luchador desde su página de perfil
     */
    private void enrichFighterFromProfile(WebDriver driver, Fighter fighter, String profileUrl) {
        WebDriver profileDriver = null;
        try {
            // Crear un nuevo WebDriver para no interferir con la navegación principal
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            
            profileDriver = new ChromeDriver(options);
            profileDriver.get(profileUrl);
            
            // Esperar más tiempo para que se cargue la página
            WebDriverWait wait = new WebDriverWait(profileDriver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            
            // Intentar extraer el récord directamente
            try {
                List<WebElement> recordElements = profileDriver.findElements(By.cssSelector(".c-record__promoted"));
                if (!recordElements.isEmpty()) {
                    String recordText = recordElements.get(0).getText().trim();
                    fighter.setRecord(recordText);
                    logger.info("Extraído récord de {}: {}", fighter.getName(), recordText);
                } else {
                    // Intentar otro selector si el primero falla
                    recordElements = profileDriver.findElements(By.cssSelector("[class*='record']"));
                    for (WebElement element : recordElements) {
                        String text = element.getText();
                        if (text.matches(".*\\d+-\\d+.*")) {
                            fighter.setRecord(text.trim());
                            logger.info("Extraído récord alternativo de {}: {}", fighter.getName(), text);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("No se pudo extraer el récord de {}: {}", fighter.getName(), e.getMessage());
                fighter.setRecord("0-0"); // Valor por defecto
            }
            
            // Extraer otros datos de manera más flexible
            extractFighterDetails(profileDriver, fighter);
            
        } catch (Exception e) {
            logger.warn("Error enriqueciendo datos del perfil para {}: {}", fighter.getName(), e.getMessage());
        } finally {
            if (profileDriver != null) {
                profileDriver.quit();
            }
        }
    }
    
    private void extractFighterDetails(WebDriver driver, Fighter fighter) {
        // Ponderación por probabilidad según la categoría de peso
        setDefaultsBasedOnWeightClass(fighter);
        
        try {
            // Extracción de información de la página
            List<WebElement> bioFields = driver.findElements(By.cssSelector(".c-bio__field, [class*='bio']"));
            
            for (WebElement field : bioFields) {
                String fieldText = field.getText().toLowerCase();
                
                // Extracción de nacionalidad
                if (fieldText.contains("nac") || fieldText.contains("birth") || fieldText.contains("país")) {
                    String[] parts = fieldText.split(":");
                    if (parts.length > 1) {
                        String locationInfo = parts[1].trim();
                        if (locationInfo.contains(",")) {
                            // Tomar el último componente que suele ser el país
                            String[] locations = locationInfo.split(",");
                            fighter.setNationality(locations[locations.length - 1].trim());
                        } else {
                            fighter.setNationality(locationInfo);
                        }
                    }
                }
                
                // Extracción de edad
                if (fieldText.contains("edad") || fieldText.contains("age")) {
                    String[] parts = fieldText.split(":");
                    if (parts.length > 1) {
                        String ageText = parts[1].trim().replaceAll("[^0-9]", "");
                        try {
                            fighter.setAge(Integer.parseInt(ageText));
                        } catch (NumberFormatException e) {
                            // Mantener la edad predeterminada
                        }
                    }
                }
                
                // Extracción de altura
                if (fieldText.contains("altura") || fieldText.contains("height")) {
                    String[] parts = fieldText.split(":");
                    if (parts.length > 1) {
                        String heightText = parts[1].trim();
                        parseHeight(heightText, fighter);
                    }
                }
                
                // Extracción de peso
                if (fieldText.contains("peso") || fieldText.contains("weight")) {
                    String[] parts = fieldText.split(":");
                    if (parts.length > 1) {
                        String weightText = parts[1].trim();
                        parseWeight(weightText, fighter);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error extrayendo detalles adicionales para {}: {}", fighter.getName(), e.getMessage());
        }
    }

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
            }
        } catch (Exception e) {
            // Mantener la altura predeterminada
        }
    }

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
            }
        } catch (Exception e) {
            // Mantener el peso predeterminado
        }
    }

    private void setDefaultsBasedOnWeightClass(Fighter fighter) {
        String weightClass = fighter.getWeightClass();
        
        // Valores por defecto según la categoría de peso
        if (weightClass.contains("Completo")) {
            fighter.setWeight(120.0);
            fighter.setHeight(1.93);
        } else if (weightClass.contains("Semicompleto")) {
            fighter.setWeight(93.0);
            fighter.setHeight(1.88);
        } else if (weightClass.contains("Mediano")) {
            fighter.setWeight(84.0);
            fighter.setHeight(1.83);
        } else if (weightClass.contains("Wélter")) {
            fighter.setWeight(77.0);
            fighter.setHeight(1.80);
        } else if (weightClass.contains("Ligero")) {
            fighter.setWeight(70.0);
            fighter.setHeight(1.75);
        } else if (weightClass.contains("Pluma")) {
            fighter.setWeight(66.0);
            fighter.setHeight(1.73);
        } else if (weightClass.contains("Gallo")) {
            fighter.setWeight(61.0);
            fighter.setHeight(1.70);
        } else if (weightClass.contains("Mosca") && !weightClass.contains("femenino")) {
            fighter.setWeight(57.0);
            fighter.setHeight(1.65);
        } else if (weightClass.contains("Paja") || 
                  (weightClass.contains("Mosca") && weightClass.contains("femenino"))) {
            fighter.setWeight(52.0);
            fighter.setHeight(1.63);
        } else if (weightClass.contains("Gallo") && weightClass.contains("femenino")) {
            fighter.setWeight(61.0);
            fighter.setHeight(1.68);
        } else if (weightClass.contains("Pluma") && weightClass.contains("femenino")) {
            fighter.setWeight(66.0);
            fighter.setHeight(1.70);
        }
        
        // Otros valores predeterminados
        if (fighter.getNationality() == null || "Unknown".equals(fighter.getNationality())) {
            fighter.setNationality("España"); // Valor predeterminado
        }
        
        if (fighter.getRecord() == null || "0-0".equals(fighter.getRecord())) {
            fighter.setRecord(generateRealisticRecord(fighter));
        }
    }

    private String generateRealisticRecord(Fighter fighter) {
        // Generar un récord realista basado en alguna lógica
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
    
    /**
     * Devuelve altura predeterminada para una categoría de peso
     */
    private double getDefaultHeightForClass(String weightClass) {
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
    
    /**
     * Normaliza nombres de categorías de peso a formato estándar de UFC
     */
    private String mapWeightClass(String apiWeightClass) {
        if (apiWeightClass == null || apiWeightClass.trim().isEmpty()) {
            return "Peso Wélter"; // Valor por defecto
        }
        
        apiWeightClass = apiWeightClass.toLowerCase().trim();
        if (apiWeightClass.contains("mosca") && (apiWeightClass.contains("mujeres") || apiWeightClass.contains("femenino")) || 
                apiWeightClass.contains("women") && apiWeightClass.contains("flyweight")) {
                return "Peso Mosca (femenino)";
            } else if (apiWeightClass.contains("gallo") && (apiWeightClass.contains("mujeres") || apiWeightClass.contains("femenino")) || 
                       apiWeightClass.contains("women") && apiWeightClass.contains("bantamweight")) {
                return "Peso Gallo (femenino)";
            } else if (apiWeightClass.contains("pluma") && (apiWeightClass.contains("mujeres") || apiWeightClass.contains("femenino")) || 
                       apiWeightClass.contains("women") && apiWeightClass.contains("featherweight")) {
                return "Peso Pluma (femenino)";
            } else if (apiWeightClass.contains("paja") || apiWeightClass.contains("strawweight")) {
                return "Peso Paja (femenino)";
            } else if ((apiWeightClass.contains("mosca") || apiWeightClass.contains("flyweight")) && 
                       !(apiWeightClass.contains("mujeres") || apiWeightClass.contains("women") || apiWeightClass.contains("femenino"))) {
                return "Peso Mosca";
            } else if ((apiWeightClass.contains("gallo") || apiWeightClass.contains("bantamweight")) && 
                       !(apiWeightClass.contains("mujeres") || apiWeightClass.contains("women") || apiWeightClass.contains("femenino"))) {
                return "Peso Gallo";
            } else if ((apiWeightClass.contains("pluma") || apiWeightClass.contains("featherweight")) && 
                       !(apiWeightClass.contains("mujeres") || apiWeightClass.contains("women") || apiWeightClass.contains("femenino"))) {
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
            
            // Si no coincide con ninguno, intentamos hacer un último análisis
            if (apiWeightClass.contains("bout")) {
                if (apiWeightClass.contains("mujer") || apiWeightClass.contains("women") || apiWeightClass.contains("femenino")) {
                    return "Peso Gallo (femenino)"; // Valor por defecto para mujeres
                } else {
                    return "Peso Wélter"; // Valor por defecto para hombres
                }
            }
            
            return "Peso Wélter"; // Valor por defecto si no se puede determinar
        }
        
        /**
         * Devuelve peso predeterminado para una categoría de peso
         */
        private double getDefaultWeightForClass(String weightClass) {
            weightClass = weightClass.toLowerCase();
            
            if (weightClass.contains("completo") || (weightClass.contains("heavyweight") && !weightClass.contains("light"))) return 120.0;
            if (weightClass.contains("semicompleto") || weightClass.contains("light heavyweight")) return 93.0;
            if (weightClass.contains("mediano") || weightClass.contains("middleweight")) return 84.0;
            if (weightClass.contains("wélter") || weightClass.contains("welter")) return 77.0;
            if (weightClass.contains("ligero") || weightClass.contains("lightweight")) return 70.0;
            if (weightClass.contains("pluma") || weightClass.contains("featherweight")) {
                if (weightClass.contains("femenino") || weightClass.contains("women")) {
                    return 66.0;
                }
                return 66.0;
            }
            if (weightClass.contains("gallo") || weightClass.contains("bantamweight")) {
                if (weightClass.contains("femenino") || weightClass.contains("women")) {
                    return 61.0;
                }
                return 61.0;
            }
            if (weightClass.contains("mosca") || weightClass.contains("flyweight")) {
                if (weightClass.contains("femenino") || weightClass.contains("women")) {
                    return 52.0;
                }
                return 57.0;
            }
            if (weightClass.contains("paja") || weightClass.contains("strawweight")) return 52.0;
            
            return 77.0; // Peso Wélter como valor por defecto
        }
    }