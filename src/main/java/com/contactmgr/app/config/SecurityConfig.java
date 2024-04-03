package com.contactmgr.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	// for password encryption
	@Bean
	BCryptPasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	// for login

	@Bean
	UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}
		
	
	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		  http.csrf().disable()
		  .authorizeHttpRequests()
		  .requestMatchers("/user/**").authenticated()
		  .anyRequest().permitAll()
		  .and()
		  .formLogin();
		  
		  http.authenticationProvider(getdaoAuthenticationProvider());
		return http.build();		
    }
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
	
	@Bean
	DaoAuthenticationProvider getdaoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(getPasswordEncoder());
		return daoAuthenticationProvider;
	}
	
	
	
	
	
}
