package com.web.paygate237.config;

import com.web.paygate237.repositories.UserRepository;
import com.web.paygate237.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Autowired
    private UserRepository userRepository;
//    private final LogoutHandler logoutHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());

//        Get Authentication Manager
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.cors().and().csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers( "/api/auth/**")
                .permitAll()
                .anyRequest().authenticated().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authenticationManager(authenticationManager)
                .authenticationProvider(authenticationProvider());
//                .logout()
//                .logoutUrl("/api/auth/logout")
//                .addLogoutHandler(logoutHandler)
//                .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()));


        http.headers().frameOptions().disable();

        return http.build();
    }

}
