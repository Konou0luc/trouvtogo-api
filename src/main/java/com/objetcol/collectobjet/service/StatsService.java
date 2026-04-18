package com.objetcol.collectobjet.service;

import com.objetcol.collectobjet.dto.response.CommunauteStatsResponse;
import com.objetcol.collectobjet.model.StatutObjet;
import com.objetcol.collectobjet.repository.ObjetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final ObjetRepository objetRepository;

    public CommunauteStatsResponse getCommunauteStats() {
        long n = objetRepository.countDistinctProprietairesByStatut(StatutObjet.ACTIF);
        return CommunauteStatsResponse.builder().personnesActives(n).build();
    }
}
