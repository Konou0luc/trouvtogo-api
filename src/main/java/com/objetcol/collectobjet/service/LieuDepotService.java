package com.objetcol.collectobjet.service;

import com.objetcol.collectobjet.dto.request.LieuDepotRequest;
import com.objetcol.collectobjet.dto.response.LieuDepotResponse;
import com.objetcol.collectobjet.exception.ResourceNotFoundException;
import com.objetcol.collectobjet.model.LieuDepot;
import com.objetcol.collectobjet.repository.LieuDepotRepository;
import com.objetcol.collectobjet.repository.ObjetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LieuDepotService {

    private final LieuDepotRepository lieuDepotRepository;
    private final ObjetRepository objetRepository;

    public List<LieuDepotResponse> listerActifsPublic() {
        return lieuDepotRepository.findByActifTrueOrderByOrdreAffichageAscNomAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<LieuDepotResponse> listerTousAdmin() {
        return lieuDepotRepository.findAll(Sort.by(Sort.Order.asc("ordreAffichage"), Sort.Order.asc("nom"))).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LieuDepotResponse getById(Long id) {
        LieuDepot lieu = lieuDepotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lieu de dépôt non trouvé avec l'ID: " + id));
        return toResponse(lieu);
    }

    @Transactional
    public LieuDepotResponse creer(LieuDepotRequest request) {
        if (lieuDepotRepository.existsByNom(request.getNom().trim())) {
            throw new IllegalArgumentException("Un lieu avec ce nom existe déjà");
        }
        LieuDepot lieu = LieuDepot.builder()
                .typeLieu(request.getTypeLieu())
                .nom(request.getNom().trim())
                .adresse(blankToNull(request.getAdresse()))
                .ville(blankToNull(request.getVille()))
                .telephone(blankToNull(request.getTelephone()))
                .indication(blankToNull(request.getIndication()))
                .actif(Boolean.TRUE.equals(request.getActif()) || request.getActif() == null)
                .ordreAffichage(Objects.requireNonNullElse(request.getOrdreAffichage(), 0))
                .build();
        return toResponse(lieuDepotRepository.save(lieu));
    }

    @Transactional
    public LieuDepotResponse modifier(Long id, LieuDepotRequest request) {
        LieuDepot lieu = lieuDepotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lieu de dépôt non trouvé avec l'ID: " + id));

        String nomTrim = request.getNom().trim();
        if (!lieu.getNom().equals(nomTrim) && lieuDepotRepository.existsByNom(nomTrim)) {
            throw new IllegalArgumentException("Un lieu avec ce nom existe déjà");
        }

        lieu.setTypeLieu(request.getTypeLieu());
        lieu.setNom(nomTrim);
        lieu.setAdresse(blankToNull(request.getAdresse()));
        lieu.setVille(blankToNull(request.getVille()));
        lieu.setTelephone(blankToNull(request.getTelephone()));
        lieu.setIndication(blankToNull(request.getIndication()));
        if (request.getActif() != null) {
            lieu.setActif(request.getActif());
        }
        lieu.setOrdreAffichage(Objects.requireNonNullElse(request.getOrdreAffichage(), lieu.getOrdreAffichage()));

        return toResponse(lieuDepotRepository.save(lieu));
    }

    @Transactional
    public void supprimer(Long id) {
        LieuDepot lieu = lieuDepotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lieu de dépôt non trouvé avec l'ID: " + id));
        long nb = objetRepository.countByLieuDepot_Id(id);
        if (nb > 0) {
            throw new IllegalArgumentException(
                    "Impossible de supprimer ce lieu : " + nb + " objet(s) y sont encore rattaché(s). Désactivez-le plutôt.");
        }
        lieuDepotRepository.delete(lieu);
    }

    private static String blankToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.trim();
    }

    private LieuDepotResponse toResponse(LieuDepot l) {
        return LieuDepotResponse.builder()
                .id(l.getId())
                .typeLieu(l.getTypeLieu())
                .nom(l.getNom())
                .adresse(l.getAdresse())
                .ville(l.getVille())
                .telephone(l.getTelephone())
                .indication(l.getIndication())
                .actif(l.isActif())
                .ordreAffichage(l.getOrdreAffichage())
                .build();
    }
}
