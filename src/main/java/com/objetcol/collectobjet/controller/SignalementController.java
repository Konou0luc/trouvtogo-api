package com.objetcol.collectobjet.controller;

import com.objetcol.collectobjet.dto.request.SignalementRequest;
import com.objetcol.collectobjet.dto.response.ApiResponse;
import com.objetcol.collectobjet.dto.response.SignalementResponse;
import com.objetcol.collectobjet.service.SignalementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Signalements", description = "Création et gestion des signalements")
public class SignalementController {

    private final SignalementService signalementService;

    @PostMapping("/api/objets/{id}/signalement")
    @Operation(summary = "Signaler une annonce", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<SignalementResponse>> create(
            @PathVariable Long id,
            @RequestBody SignalementRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        SignalementResponse res = signalementService.createSignalement(id, userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Signalement créé", res));
    }

    @GetMapping("/api/admin/signalements")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les signalements (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<SignalementResponse>>> listAll() {
        return ResponseEntity.ok(ApiResponse.success("Signalements", signalementService.listAll()));
    }

    @GetMapping("/api/admin/signalements/non-resolus")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister signalements non résolus (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<SignalementResponse>>> listUnresolved() {
        return ResponseEntity.ok(ApiResponse.success("Signalements non résolus", signalementService.listUnresolved()));
    }

    @PatchMapping("/api/admin/signalements/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Marquer un signalement comme résolu (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<SignalementResponse>> resolve(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        SignalementResponse res = signalementService.resolve(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Signalement résolu", res));
    }
}
