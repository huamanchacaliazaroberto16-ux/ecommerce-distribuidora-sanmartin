package com.distribuidora.sanmartin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactivamos CSRF temporalmente para facilitar tus pruebas
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/tienda", "/login", "/registro", "/css/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin", true) // Te lleva directo al panel admin tras entrar
                .permitAll()
            )
            .logout(logout -> logout.permitAll());
        
        return http.build();
    }
}