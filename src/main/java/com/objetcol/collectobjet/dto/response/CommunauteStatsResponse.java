package com.objetcol.collectobjet.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Indicateurs publics sur la communauté")
public class CommunauteStatsResponse {

    @Schema(description = "Personnes distinctes ayant au moins un signalement au statut ACTIF")
    private long personnesActives;
}
