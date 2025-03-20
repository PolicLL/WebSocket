package com.example.websocketdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  @Bean
  public SecurityFilterChain securityWebFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(request -> request
            .requestMatchers("/chat/**").permitAll()
            .anyRequest().permitAll())
        .build();
  }
}
