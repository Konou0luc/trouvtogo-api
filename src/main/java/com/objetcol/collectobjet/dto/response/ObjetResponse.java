package com.objetcol.collectobjet.dto.response;

import com.objetcol.collectobjet.model.StatutObjet;
import com.objetcol.collectobjet.model.TypeObjet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjetResponse {

    private Long id;
    private String titre;
    private String description;
    private TypeObjet type;
    private StatutObjet statut;
    private String localisation;
    private LocalDateTime dateEvenement;
    private String categorieNom;
    /** Libellé lisible (ex. description de la catégorie côté référentiel) */
    private String categorieDescription;
    private Long categorieId;
    private String proprietaireUsername;
    private Long proprietaireId;
    private List<String> photosUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}