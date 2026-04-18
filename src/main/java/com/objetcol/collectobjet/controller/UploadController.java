package com.objetcol.collectobjet.controller;

import com.objetcol.collectobjet.dto.response.ApiResponse;
import com.objetcol.collectobjet.service.ImageUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Tag(name = "Upload", description = "Envoi d'images vers Cloudinary")
@SecurityRequirement(name = "bearerAuth")
public class UploadController {

    private final ImageUploadService imageUploadService;

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Téléverser une ou plusieurs images (max 5)")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> uploadImages(
            @RequestParam("files") MultipartFile[] files) throws IOException {
        if (files != null && files.length > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Maximum 5 images par requête"));
        }
        List<String> urls = imageUploadService.uploadImages(files);
        Map<String, List<String>> body = new HashMap<>();
        body.put("urls", urls);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Images téléversées", body));
    }
}
