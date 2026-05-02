package com.objetcol.collectobjet.config;

import com.objetcol.collectobjet.model.Role;
import com.objetcol.collectobjet.model.User;
import com.objetcol.collectobjet.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class InitialAdminUserConfig {

    @Bean
    @ConditionalOnProperty(prefix = "app.init-admin", name = "enabled", havingValue = "true")
    public CommandLineRunner createInitialAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String email = System.getenv().getOrDefault("INIT_ADMIN_EMAIL", "kiiakoluc@gmail.com");
            String username = System.getenv().getOrDefault("INIT_ADMIN_USERNAME", "Admin");
            String rawPassword = System.getenv().getOrDefault("INIT_ADMIN_PASSWORD", "luc12345!");

            if (!userRepository.existsByEmailIgnoreCase(email) && !userRepository.existsByUsername(username)) {
                User admin = User.builder()
                        .username(username)
                        .email(email)
                        .password(passwordEncoder.encode(rawPassword))
                        .role(Role.ROLE_ADMIN)
                        .actif(true)
                        .build();

                userRepository.save(admin);
                System.out.println("Initial admin user created: " + email);
            } else {
                System.out.println("Initial admin user already exists, skipping creation.");
            }
        };
    }
}
