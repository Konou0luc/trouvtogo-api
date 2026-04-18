package com.objetcol.collectobjet.controller;

import com.objetcol.collectobjet.dto.response.ApiResponse;
import com.objetcol.collectobjet.dto.response.CommunauteStatsResponse;
import com.objetcol.collectobjet.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Tag(name = "Statistiques", description = "Indicateurs publics")
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/communaute")
    @Operation(summary = "Statistiques communauté (personnes actives sur la plateforme)")
    public ResponseEntity<ApiResponse<CommunauteStatsResponse>> communaute() {
        return ResponseEntity.ok(
                ApiResponse.success("Statistiques communauté", statsService.getCommunauteStats()));
    }
}
