package com.objetcol.collectobjet.controller;

import com.objetcol.collectobjet.dto.request.ObjetRequest;
import com.objetcol.collectobjet.dto.response.ApiResponse;
import com.objetcol.collectobjet.dto.response.ObjetResponse;
import com.objetcol.collectobjet.model.StatutObjet;
import com.objetcol.collectobjet.model.TypeObjet;
import com.objetcol.collectobjet.service.ObjetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/objets")
@RequiredArgsConstructor
@Tag(name = "Objets", description = "Gestion des objets perdus et trouvés")
public class ObjetController {

    private final ObjetService objetService;

    @PostMapping
    @Operation(summary = "Publier un nouvel objet", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ObjetResponse>> creerObjet(
            @Valid @RequestBody ObjetRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ObjetResponse response = objetService.creerObjet(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Objet publié avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les objets avec filtres optionnels")
    public ResponseEntity<ApiResponse<Page<ObjetResponse>>> listerObjets(
            @RequestParam(required = false) TypeObjet type,
            @RequestParam(required = false) StatutObjet statut,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<ObjetResponse> page = objetService.listerObjets(type, statut, pageable);
        return ResponseEntity.ok(ApiResponse.success("Liste des objets", page));
    }

    @GetMapping("/recherche")
    @Operation(summary = "Rechercher des objets avec mots-clés")
    public ResponseEntity<ApiResponse<Page<ObjetResponse>>> rechercher(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TypeObjet type,
            @RequestParam(required = false) StatutObjet statut,
            @RequestParam(required = false) Long categorieId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ObjetResponse> result = objetService.rechercher(keyword, type, statut, categorieId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Résultats de la recherche", result));
    }

    @GetMapping("/mes-objets")
    @Operation(summary = "Lister mes propres objets", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<ObjetResponse>>> getMesObjets(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ObjetResponse> objets = objetService.getMesObjets(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Mes objets", objets));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un objet par son ID")
    public ResponseEntity<ApiResponse<ObjetResponse>> getObjet(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Objet trouvé", objetService.getObjetById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un objet", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ObjetResponse>> modifierObjet(
            @PathVariable Long id,
            @Valid @RequestBody ObjetRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ObjetResponse response = objetService.modifierObjet(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Objet modifié avec succès", response));
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Changer le statut d'un objet", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ObjetResponse>> changerStatut(
            @PathVariable Long id,
            @RequestParam StatutObjet statut,
            @AuthenticationPrincipal UserDetails userDetails) {
        ObjetResponse response = objetService.changerStatut(id, statut, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Statut mis à jour", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un objet", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> supprimerObjet(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        objetService.supprimerObjet(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Objet supprimé avec succès"));
    }
}