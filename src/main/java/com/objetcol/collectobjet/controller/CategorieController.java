package com.objetcol.collectobjet.controller;

import com.objetcol.collectobjet.dto.response.ApiResponse;
import com.objetcol.collectobjet.dto.response.CategorieResponse;
import com.objetcol.collectobjet.service.CategorieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Catégories", description = "Liste des catégories d'objets")
public class CategorieController {

    private final CategorieService categorieService;

    @GetMapping
    @Operation(summary = "Lister toutes les catégories")
    public ResponseEntity<ApiResponse<List<CategorieResponse>>> lister() {
        return ResponseEntity.ok(ApiResponse.success("Catégories", categorieService.listerToutes()));
    }
}
