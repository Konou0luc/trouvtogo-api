package com.objetcol.collectobjet.controller;

import com.objetcol.collectobjet.dto.request.ChangePasswordRequest;
import com.objetcol.collectobjet.dto.request.UpdateProfileRequest;
import com.objetcol.collectobjet.dto.response.ApiResponse;
import com.objetcol.collectobjet.dto.response.AuthResponse;
import com.objetcol.collectobjet.dto.response.UserProfileResponse;
import com.objetcol.collectobjet.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Profil du compte connecté")
public class UserController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    @Operation(summary = "Profil de l'utilisateur connecté")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMe(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentification requise"));
        }
        UserProfileResponse profile = userProfileService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Profil chargé", profile));
    }

    @PutMapping("/me")
    @Operation(summary = "Mettre à jour le profil (un nouveau jeton JWT est renvoyé si l'email change)")
    public ResponseEntity<ApiResponse<AuthResponse>> updateMe(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentification requise"));
        }
        AuthResponse auth = userProfileService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Profil mis à jour", auth));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Changer le mot de passe")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentification requise"));
        }
        userProfileService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Mot de passe mis à jour"));
    }
}
