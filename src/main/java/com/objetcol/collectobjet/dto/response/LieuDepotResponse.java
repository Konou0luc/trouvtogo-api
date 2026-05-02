package com.objetcol.collectobjet.dto.response;

import com.objetcol.collectobjet.model.TypeLieuDepot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LieuDepotResponse {

    private Long id;
    private TypeLieuDepot typeLieu;
    private String nom;
    private String adresse;
    private String ville;
    private String telephone;
    private String indication;
    private boolean actif;
    private Integer ordreAffichage;
}
