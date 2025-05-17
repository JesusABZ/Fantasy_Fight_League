package com.fantasyfightleague.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;
import java.util.Collections;

@Configuration
public class AppConfig {
    
    @Value("${fightingtomatoes.api.username}")
    private String apiUsername;
    
    @Value("${fightingtomatoes.api.password}")
    private String apiPassword;
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Agregar interceptor para la autenticación básica
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            // Crear el header de autenticación básica
            String auth = apiUsername + ":" + apiPassword;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
            String authHeader = "Basic " + new String(encodedAuth);
            
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, authHeader);
            return execution.execute(request, body);
        };
        
        restTemplate.setInterceptors(Collections.singletonList(interceptor));
        
        return restTemplate;
    }
}