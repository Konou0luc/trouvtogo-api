package com.objetcol.collectobjet.dto.request;

import com.objetcol.collectobjet.model.TypeObjet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ObjetRequest {

    @NotBlank(message = "Le titre est requis")
    private String titre;

    private String description;

    @NotNull(message = "Le type est requis (PERDU ou TROUVE)")
    private TypeObjet type;

    private String localisation;

    private LocalDateTime dateEvenement;

    private Long categorieId;

    private List<String> photosUrls;
}