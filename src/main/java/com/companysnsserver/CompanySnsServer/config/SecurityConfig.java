package com.companysnsserver.CompanySnsServer.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/**", "/file/upload", "/image/upload" ).permitAll() // 인증 없이 접근 가능
                                //.requestMatchers("/api/register", "/api/login", "/file/upload", "/api/profile/**", "/image/upload","/api/images/**" ,"/api/followingPosts/", "/api/search", "/api/follow" , "/api/followers" , "/api/unfollow" , "/api/isFollowing" , "/api/connections" , "/api/allPosts" ).permitAll() // 인증 없이 접근 가능
                                .anyRequest().authenticated() // 다른 모든 요청은 인증 필요
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
