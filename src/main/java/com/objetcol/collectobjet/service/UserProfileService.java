package com.objetcol.collectobjet.service;

import com.objetcol.collectobjet.dto.request.ChangePasswordRequest;
import com.objetcol.collectobjet.dto.request.UpdateProfileRequest;
import com.objetcol.collectobjet.dto.response.AuthResponse;
import com.objetcol.collectobjet.dto.response.UserProfileResponse;
import com.objetcol.collectobjet.exception.ResourceNotFoundException;
import com.objetcol.collectobjet.model.User;
import com.objetcol.collectobjet.repository.UserRepository;
import com.objetcol.collectobjet.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return toResponse(user);
    }

    @Transactional
    public AuthResponse updateProfile(String currentEmail, UpdateProfileRequest request) {
        User user = userRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        String newUsername = request.getUsername().trim();
        String newEmail = request.getEmail().trim().toLowerCase();

        if (!newUsername.equalsIgnoreCase(user.getUsername())
                && userRepository.existsByUsernameAndIdNot(newUsername, user.getId())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris");
        }
        if (!newEmail.equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmailIgnoreCaseAndIdNot(newEmail, user.getId())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        user.setUsername(newUsername);
        user.setEmail(newEmail);
        user.setTelephone(emptyToNull(request.getTelephone()));

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .role(user.getRole())
                .build();
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mot de passe actuel incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private static UserProfileResponse toResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private static String emptyToNull(String s) {
        return StringUtils.hasText(s) ? s.trim() : null;
    }
}
