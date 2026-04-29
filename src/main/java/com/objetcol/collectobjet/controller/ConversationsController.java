package com.objetcol.collectobjet.controller;

import com.objetcol.collectobjet.dto.response.ApiResponse;
import com.objetcol.collectobjet.dto.response.ConversationResponse;
import com.objetcol.collectobjet.dto.response.MessageResponse;
import com.objetcol.collectobjet.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Tag(name = "Conversations", description = "Listes et échanges entre utilisateurs")
@SecurityRequirement(name = "bearerAuth")
public class ConversationsController {

    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "Lister les conversations de l'utilisateur connecté")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> list(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ConversationResponse> convs = messageService.getConversations(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Conversations", convs));
    }

    @GetMapping("/{otherUserId}/messages")
    @Operation(summary = "Obtenir les messages d'une conversation avec un autre utilisateur")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> messages(
            @PathVariable Long otherUserId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MessageResponse> msgs = messageService.getConversation(otherUserId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Conversation", msgs));
    }
}
