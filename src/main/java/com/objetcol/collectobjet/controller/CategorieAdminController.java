package com.objetcol.collectobjet.controller;

import com.objetcol.collectobjet.dto.request.CategorieRequest;
import com.objetcol.collectobjet.dto.response.ApiResponse;
import com.objetcol.collectobjet.dto.response.CategorieResponse;
import com.objetcol.collectobjet.service.CategorieService;
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
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Tag(name = "Catégories - Admin", description = "Gestion admin des catégories")
@PreAuthorize("hasRole('ADMIN')")
public class CategorieAdminController {

    private final CategorieService categorieService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle catégorie", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<CategorieResponse>> creerCategorie(
            @Valid @RequestBody CategorieRequest request) {
        CategorieResponse response = categorieService.creerCategorie(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Catégorie créée avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister toutes les catégories (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<CategorieResponse>>> listerToutes() {
        return ResponseEntity.ok(ApiResponse.success("Liste des catégories", categorieService.listerToutes()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une catégorie par son ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<CategorieResponse>> getCategorie(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Catégorie trouvée", categorieService.getCategorieById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une catégorie", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<CategorieResponse>> modifierCategorie(
            @PathVariable Long id,
            @Valid @RequestBody CategorieRequest request) {
        CategorieResponse response = categorieService.modifierCategorie(id, request);
        return ResponseEntity.ok(ApiResponse.success("Catégorie modifiée avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une catégorie", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> supprimerCategorie(@PathVariable Long id) {
        categorieService.supprimerCategorie(id);
        return ResponseEntity.ok(ApiResponse.success("Catégorie supprimée avec succès"));
    }
}
