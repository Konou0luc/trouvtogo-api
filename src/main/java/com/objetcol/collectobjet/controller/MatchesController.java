package com.objetcol.collectobjet.controller;

import com.objetcol.collectobjet.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
@Tag(name = "Matches", description = "Points de correspondance (placeholder)")
public class MatchesController {

    @GetMapping
    @Operation(summary = "Lister les matches pour un utilisateur (placeholder)")
    public ResponseEntity<ApiResponse<List<Object>>> list(@RequestParam(required = false) Long userId) {
        // Placeholder implementation — return empty list to keep frontend stable
        return ResponseEntity.ok(ApiResponse.success("Matches (placeholder)", Collections.emptyList()));
    }
}
