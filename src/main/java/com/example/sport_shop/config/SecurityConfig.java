package com.example.sportshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/home").permitAll() // Разрешаем доступ к главной странице всем
                        .requestMatchers("/css/**", "/images/**").permitAll() // Доступ к статическим ресурсам
                        .anyRequest().authenticated() // Все остальные маршруты требуют аутентификации
                )
                .formLogin(form -> form
                        .loginPage("/login") // Страница входа
                        .permitAll() // Разрешаем доступ к странице входа всем
                )
                .logout(logout -> logout
                        .permitAll() // Разрешаем выход всем
                );

        return http.build();
    }
}