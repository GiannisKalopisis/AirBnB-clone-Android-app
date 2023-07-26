package com.dit.airbnb.security.config;

import com.dit.airbnb.security.jwt.JwtAuthenticationEntryPoint;
import com.dit.airbnb.security.jwt.JwtAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.dit.airbnb.security.config.SecurityConstants.SIGN_IN_URL;
import static com.dit.airbnb.security.config.SecurityConstants.SIGN_UP_URL;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private ApplicationConfig authenticationConfig;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).cors(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/**").permitAll().requestMatchers(SIGN_UP_URL).permitAll().requestMatchers(SIGN_IN_URL).permitAll().anyRequest().authenticated())
                .authenticationProvider(authenticationConfig.authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}