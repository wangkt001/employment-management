package com.employment.config;

import com.employment.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private UserRepository userRepository;

    public SecurityConfig() {
        System.out.println("SecurityConfig实例化");
        logger.debug("SecurityConfig实例化");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("开始配置SecurityFilterChain");
        logger.debug("开始配置SecurityFilterChain");

        http
                .csrf(csrf -> {
                    System.out.println("禁用CSRF保护");
                    logger.debug("禁用CSRF保护");
                    csrf.disable();
                })
                .cors(cors -> cors.configurationSource(request -> {
                    System.out.println("配置CORS策略");
                    logger.debug("配置CORS策略");
                    org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOrigins(java.util.Arrays.asList("http://localhost:3000", "http://localhost:3001",
                            "http://localhost:3002"));
                    config.setAllowedMethods(
                            java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    config.setAllowedHeaders(java.util.Arrays.asList("*"));
                    config.setAllowCredentials(true);
                    System.out.println(
                            "CORS配置完成: 允许来源=" + config.getAllowedOrigins() + ", 允许方法=" + config.getAllowedMethods());
                    logger.debug("CORS配置完成: 允许来源={}, 允许方法={}", config.getAllowedOrigins(), config.getAllowedMethods());
                    return config;
                }))
                .authorizeHttpRequests(authorize -> {
                    System.out.println("配置请求授权规则");
                    logger.debug("配置请求授权规则");
                    authorize
                            .requestMatchers("/api/company/jobs").permitAll() // 精确匹配
                            .requestMatchers("/api/**").permitAll()
                            .requestMatchers("/employment/api/**").permitAll()
                            .requestMatchers("/login").permitAll()
                            .requestMatchers("/logout").permitAll()
                            .requestMatchers("/employment/login").permitAll()
                            .requestMatchers("/employment/logout").permitAll()
                            .requestMatchers("/", "/register", "/css/**", "/js/**", "/assets/**").permitAll()
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .requestMatchers("/company/**").hasAnyRole("COMPANY", "ADMIN")
                            .requestMatchers("/student/**").hasAnyRole("STUDENT", "ADMIN")
                            .anyRequest().permitAll();
                    System.out.println("请求授权规则配置完成");
                    logger.debug("请求授权规则配置完成");
                })
                .formLogin(form -> form
                        .loginPage("/employment/index.html")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/api/users/current", true) // 登录成功后返回用户信息
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/employment/index.html")
                        .permitAll())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                        .maximumSessions(1))
                .rememberMe(remember -> remember
                        .key("employment-system-key")
                        .tokenValiditySeconds(86400)); // 24小时

        System.out.println("SecurityFilterChain配置完成");
        logger.debug("SecurityFilterChain配置完成");
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        System.out.println("配置UserDetailsService");
        logger.debug("配置UserDetailsService");
        return username -> {
            try {
                System.out.println("查找用户: " + username);
                logger.debug("查找用户: {}", username);
                var user = userRepository.findByUsername(username);
                if (user != null) {
                    System.out.println("找到用户: " + user.getUsername() + ", 角色: " + user.getRole());
                    logger.debug("找到用户: {}, 角色: {}", user.getUsername(), user.getRole());
                    return org.springframework.security.core.userdetails.User
                            .withUsername(user.getUsername())
                            .password(user.getPassword())
                            .authorities("ROLE_" + user.getRole())
                            .build();
                }
                System.out.println("未找到用户: " + username);
                logger.debug("未找到用户: {}", username);
            } catch (Exception e) {
                System.out.println("数据库查询异常: " + e.getMessage());
                logger.debug("数据库查询异常: {}", e.getMessage());
                // 数据库连接失败时，使用默认的 admin 用户
                if ("admin".equals(username)) {
                    System.out.println("使用默认admin用户");
                    logger.debug("使用默认admin用户");
                    return org.springframework.security.core.userdetails.User
                            .withUsername("admin")
                            .password(passwordEncoder().encode("admin123"))
                            .authorities("ROLE_ADMIN")
                            .build();
                }
                throw new UsernameNotFoundException("User not found");
            }
            // 如果数据库中没有用户，使用默认的 admin 用户
            if ("admin".equals(username)) {
                System.out.println("使用默认admin用户");
                logger.debug("使用默认admin用户");
                return org.springframework.security.core.userdetails.User
                        .withUsername("admin")
                        .password(passwordEncoder().encode("admin123"))
                        .authorities("ROLE_ADMIN")
                        .build();
            }
            System.out.println("抛出UsernameNotFoundException: " + username);
            logger.debug("抛出UsernameNotFoundException: {}", username);
            throw new UsernameNotFoundException("User not found");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("配置PasswordEncoder");
        logger.debug("配置PasswordEncoder");
        return new BCryptPasswordEncoder();
    }
}