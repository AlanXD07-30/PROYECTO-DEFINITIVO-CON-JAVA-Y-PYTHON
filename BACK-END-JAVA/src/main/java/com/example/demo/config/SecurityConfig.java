package com.example.demo.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

    /**
     * PasswordEncoder principal usado actualmente (NoOp) para aceptar contraseñas en texto plano.
     * Mantener solo para pruebas/migración. En producción reemplazar por BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * Bean BCrypt disponible para otras dependencias que lo requieran.
     * No cambia el encoder principal usado por DaoAuthenticationProvider en esta configuración.
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * DaoAuthenticationProvider configurado con el PasswordEncoder principal (NoOp actualmente).
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Security filter chain.
     *
     * Cambios clave:
     * - Permitimos explícitamente POST /perform_login para que tu LoginController lo maneje.
     * - Cambiamos loginProcessingUrl de Spring a /login_proc para evitar que Spring
     *   intercepte /perform_login.
     * - Ignoramos CSRF en /registrar y /perform_login (útil para SPA/testing).
     * - Usamos el CorsConfigurationSource inyectado por @Qualifier("appCorsConfigurationSource").
     * - Permitimos acceso público (GET) a /java/inmuebles y sus subrutas para que la lista de inmuebles
     *   pueda consultarse desde el frontend sin autenticación.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           DaoAuthenticationProvider authProvider,
                                           @Qualifier("appCorsConfigurationSource") CorsConfigurationSource corsSource) throws Exception {
        http
            .httpBasic(httpBasic -> httpBasic.disable())
            .cors(cors -> cors.configurationSource(corsSource))
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    new AntPathRequestMatcher("/registrar"),
                    new AntPathRequestMatcher("/perform_login")
                )
            )
            .authenticationProvider(authProvider)
            .authorizeHttpRequests(auth -> auth
                // permitir registro y procesamiento de login por tu controlador
                .requestMatchers(HttpMethod.POST, "/registrar").permitAll()
                .requestMatchers(HttpMethod.POST, "/perform_login").permitAll()
                // permitir recursos estáticos y páginas públicas
                .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/public/**", "/registro.html", "/registro").permitAll()
                // permitir acceso público a la API de inmuebles (GET)
                .requestMatchers(HttpMethod.GET, "/java/inmuebles", "/java/inmuebles/**").permitAll()
                // resto requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login_proc") // Spring usa esta URL internamente; tu controlador usa /perform_login
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }
}
