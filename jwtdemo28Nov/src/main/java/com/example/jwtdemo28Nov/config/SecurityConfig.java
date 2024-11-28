package com.example.jwtdemo28Nov.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.jwtdemo28Nov.jwt.JwtAuthFilter;
import com.example.jwtdemo28Nov.service.UserDetailsServiceImpl;

import jakarta.servlet.Filter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	JwtAuthFilter jwtAuthFilter;
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}
	
	 @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		 
		 return http
				 .csrf(csrf->csrf.disable())
				 .authorizeHttpRequests(auth->auth
						 .requestMatchers("/api/home","/api/login", "/api/register")
						 .permitAll()
						 .anyRequest().authenticated())
				 .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				 .authenticationProvider(authenticationProvider())
				 .addFilterBefore(jwtAuthFilter, (Class<? extends Filter>) UsernamePasswordAuthenticationFilter.class)
				 .build();
		 
//	        return http
//	                .csrf().disable()
//	                .authorizeHttpRequests((authorize) -> authorize
//	                        .requestMatchers("/api/v1/login").permitAll()
//	                        .anyRequest().authenticated()
//	                )
//	                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//	                .authenticationProvider(authenticationProvider())
//	                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//	                .build();
	    }

	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	    @Bean
	    public AuthenticationProvider authenticationProvider() {
	        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
	        authenticationProvider.setUserDetailsService(userDetailsService());
	        authenticationProvider.setPasswordEncoder(passwordEncoder());
	        return authenticationProvider;

	    }

	    @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	        return config.getAuthenticationManager();
	    }
}
