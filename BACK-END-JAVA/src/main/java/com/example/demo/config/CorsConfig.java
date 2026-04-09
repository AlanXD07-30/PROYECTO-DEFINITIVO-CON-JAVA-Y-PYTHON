package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    /**
     * Configuración CORS centralizada.
     * El bean se registra con nombre "appCorsConfigurationSource" para evitar ambigüedades
     * al inyectarlo en SecurityConfig.
     */
    @Bean("appCorsConfigurationSource")
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes del frontend (ajusta si usas otro host/puerto)
        config.setAllowedOrigins(List.of(
            "http://127.0.0.1:5500",
            "http://localhost:5500"
        ));

        // Métodos permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos en la petición
        config.setAllowedHeaders(List.of(
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "X-XSRF-TOKEN",
            "X-CSRF-TOKEN",
            "Authorization"
        ));

        // Permitir envío de cookies/credenciales
        config.setAllowCredentials(true);

        // Headers que el navegador puede leer en la respuesta
        config.setExposedHeaders(List.of("Location"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
