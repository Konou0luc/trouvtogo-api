package com.objetcol.collectobjet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequest {

    @NotNull(message = "Le destinataire est obligatoire")
    private Long destinataireId;

    @NotBlank(message = "Le contenu ne peut pas être vide")
    private String contenu;

    private Long objetId;
}