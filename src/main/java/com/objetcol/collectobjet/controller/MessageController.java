package com.objetcol.collectobjet.controller;

import com.objetcol.collectobjet.dto.request.MessageRequest;
import com.objetcol.collectobjet.dto.response.ApiResponse;
import com.objetcol.collectobjet.dto.response.MessageResponse;
import com.objetcol.collectobjet.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Messagerie entre utilisateurs")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Envoyer un message à un utilisateur")
    public ResponseEntity<ApiResponse<MessageResponse>> envoyerMessage(
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        MessageResponse response = messageService.envoyerMessage(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message envoyé", response));
    }

    @GetMapping("/recus")
    @Operation(summary = "Obtenir les messages reçus")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessagesRecus(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MessageResponse> messages = messageService.getMessagesRecus(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Messages reçus", messages));
    }

    @GetMapping("/envoyes")
    @Operation(summary = "Obtenir les messages envoyés")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessagesEnvoyes(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MessageResponse> messages = messageService.getMessagesEnvoyes(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Messages envoyés", messages));
    }

    @GetMapping("/conversation/{autreUserId}")
    @Operation(summary = "Obtenir la conversation complète avec un utilisateur")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getConversation(
            @PathVariable Long autreUserId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MessageResponse> messages = messageService.getConversation(autreUserId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Conversation", messages));
    }

    @PatchMapping("/{id}/lu")
    @Operation(summary = "Marquer un message comme lu")
    public ResponseEntity<ApiResponse<Void>> marquerCommeLu(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        messageService.marquerCommeLu(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Message marqué comme lu"));
    }

    @GetMapping("/non-lus/count")
    @Operation(summary = "Obtenir le nombre de messages non lus")
    public ResponseEntity<ApiResponse<Long>> getNombreNonLus(
            @AuthenticationPrincipal UserDetails userDetails) {
        long count = messageService.getNombreMessagesNonLus(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Messages non lus", count));
    }
}