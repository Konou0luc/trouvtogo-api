package com.objetcol.collectobjet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private Long id;
    private String contenu;
    private boolean lu;
    private Long expediteurId;
    private String expediteurUsername;
    private Long destinataireId;
    private String destinataireUsername;
    private Long objetId;
    private String objetTitre;
    private LocalDateTime createdAt;
}