package com.objetcol.collectobjet.controller;

import com.objetcol.collectobjet.dto.request.LieuDepotRequest;
import com.objetcol.collectobjet.dto.response.ApiResponse;
import com.objetcol.collectobjet.dto.response.LieuDepotResponse;
import com.objetcol.collectobjet.service.LieuDepotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lieux-depot")
@RequiredArgsConstructor
@Tag(name = "Lieux de dépôt - Admin", description = "Gestion des lieux où les trouveurs peuvent déposer un objet")
@PreAuthorize("hasRole('ADMIN')")
public class LieuDepotAdminController {

    private final LieuDepotService lieuDepotService;

    @PostMapping
    @Operation(summary = "Créer un lieu de dépôt", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<LieuDepotResponse>> creer(@Valid @RequestBody LieuDepotRequest request) {
        LieuDepotResponse response = lieuDepotService.creer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lieu de dépôt créé", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les lieux (y compris inactifs)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<LieuDepotResponse>>> lister() {
        return ResponseEntity.ok(ApiResponse.success("Liste des lieux", lieuDepotService.listerTousAdmin()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un lieu", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<LieuDepotResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Lieu trouvé", lieuDepotService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un lieu", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<LieuDepotResponse>> modifier(
            @PathVariable Long id,
            @Valid @RequestBody LieuDepotRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Lieu mis à jour", lieuDepotService.modifier(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un lieu (si aucun objet rattaché)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> supprimer(@PathVariable Long id) {
        lieuDepotService.supprimer(id);
        return ResponseEntity.ok(ApiResponse.success("Lieu supprimé"));
    }
}
