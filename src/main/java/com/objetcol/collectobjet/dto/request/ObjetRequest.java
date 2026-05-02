package com.objetcol.collectobjet.dto.request;

import com.objetcol.collectobjet.model.ConservationTrouvaille;
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

    /** Obligatoire si {@code type == TROUVE} : où se trouve l’objet. */
    private ConservationTrouvaille conservationTrouve;

    /** Obligatoire si {@code conservationTrouve == DEPOSE_STRUCTURE}. */
    private Long lieuDepotId;

    /** Point GPS de la découverte (avec {@code longitude}), facultatif. */
    private Double latitude;

    private Double longitude;

    private List<String> photosUrls;

    /**
     * Optionnel : montant en FCFA pour le retrouveur, uniquement si {@code type == PERDU}.
     */
    private Long remiseMontant;
}