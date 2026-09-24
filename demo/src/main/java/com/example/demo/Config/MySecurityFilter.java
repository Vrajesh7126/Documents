package com.example.demo.Config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component 
public class MySecurityFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        System.out.println(">>> Before Security Filter: " + request.getRequestURI());

        filterChain.doFilter(request, response);

        System.out.println(">>> After Security Filter: " + request.getRequestURI());
    }

    @Bean
    UserDetailsService userDetailsService() {
        UserDetails user = org.springframework.security.core.userdetails.User.withUsername("vrajesh").password("{noop}1234").roles("USER").build();

        UserDetails admin = org.springframework.security.core.userdetails.User.withUsername("admin").password("{noop}1234").roles("ADMIN").build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}


// @Configuration 
// public class SecurityConfig {
    
//     @Bean 
//     SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//         return http.
//             authorizeHttpRequests(auth -> auth.
//                 requestMatchers("/vrajesh/public").permitAll().
//                 requestMatchers("/vrajesh/private").hasRole("ADMIN")
//                 .anyRequest().authenticated()
//             )
//             .httpBasic(Customizer.withDefaults())
//             .build();
//     }

//     @Bean
//     UserDetailsService userDetailsService() {
//         UserDetails user = org.springframework.security.core.userdetails.User.withUsername("vrajesh").password("{noop}1234").roles("USER").build();

//         UserDetails admin = org.springframework.security.core.userdetails.User.withUsername("admin").password("{noop}1234").roles("ADMIN").build();

//         return new InMemoryUserDetailsManager(user, admin);
//     }
// }
