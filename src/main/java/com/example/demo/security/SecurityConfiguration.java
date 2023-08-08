package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
/**
 * NB we turn off Auto Config where @SpringBootApplication is defined to allow
 * this config to take over ....
 * NB No security defined at the moment ....
 */
public class SecurityConfiguration {

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    /**
     * i.e. ..anyone can create a user, everything else needs auth and role springboot-user.
     */
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .requestMatchers(HttpMethod.POST, "/users").permitAll() // allowed
                .requestMatchers(HttpMethod.GET, "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/actuator/health").permitAll() // allowed
                // everything else
                .anyRequest().hasRole("springboot-user")
        ;
        // http.oauth2Login();
        http.oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter());
        return http.build();
    }

}