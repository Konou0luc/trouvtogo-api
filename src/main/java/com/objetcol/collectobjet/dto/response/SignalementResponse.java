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
public class SignalementResponse {
    private Long id;
    private Long objetId;
    private String objetTitre;
    private Long reporterId;
    private String reporterUsername;
    private String message;
    private boolean resolved;
    private Long resolverId;
    private String resolverUsername;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
