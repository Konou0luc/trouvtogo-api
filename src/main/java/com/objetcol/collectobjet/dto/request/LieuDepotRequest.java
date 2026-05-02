package com.objetcol.collectobjet.dto.request;

import com.objetcol.collectobjet.model.TypeLieuDepot;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LieuDepotRequest {

    @NotNull(message = "Le type de lieu est requis")
    private TypeLieuDepot typeLieu;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 200)
    private String nom;

    @Size(max = 500)
    private String adresse;

    @Size(max = 120)
    private String ville;

    @Size(max = 40)
    private String telephone;

    @Size(max = 1000)
    private String indication;

    /** Si absent en JSON, traité comme {@code true} côté service. */
    private Boolean actif;

    /** Si absent, traité comme {@code 0} côté service. */
    private Integer ordreAffichage;
}
