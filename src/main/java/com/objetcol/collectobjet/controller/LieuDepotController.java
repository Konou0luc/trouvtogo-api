package com.objetcol.collectobjet.controller;

import com.objetcol.collectobjet.dto.response.ApiResponse;
import com.objetcol.collectobjet.dto.response.LieuDepotResponse;
import com.objetcol.collectobjet.service.LieuDepotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lieux-depot")
@RequiredArgsConstructor
@Tag(name = "Lieux de dépôt", description = "Lieux où déposer un objet trouvé (liste publique, actifs uniquement)")
public class LieuDepotController {

    private final LieuDepotService lieuDepotService;

    @GetMapping
    @Operation(summary = "Lister les lieux de dépôt disponibles")
    public ResponseEntity<ApiResponse<List<LieuDepotResponse>>> lister() {
        return ResponseEntity.ok(ApiResponse.success("Lieux de dépôt", lieuDepotService.listerActifsPublic()));
    }
}
