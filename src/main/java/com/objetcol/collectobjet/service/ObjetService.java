package com.objetcol.collectobjet.service;

import com.objetcol.collectobjet.dto.request.ObjetRequest;
import com.objetcol.collectobjet.dto.response.ObjetResponse;
import com.objetcol.collectobjet.exception.ResourceNotFoundException;
import com.objetcol.collectobjet.exception.UnauthorizedException;
import com.objetcol.collectobjet.model.*;
import com.objetcol.collectobjet.repository.CategorieRepository;
import com.objetcol.collectobjet.repository.ObjetRepository;
import com.objetcol.collectobjet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObjetService {

    private final ObjetRepository objetRepository;
    private final UserRepository userRepository;
    private final CategorieRepository categorieRepository;

    @Transactional
    public ObjetResponse creerObjet(ObjetRequest request, String email) {
        User proprietaire = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        Categorie categorie = null;
        if (request.getCategorieId() != null) {
            categorie = categorieRepository.findById(request.getCategorieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categorie", request.getCategorieId()));
        }

        Objet objet = Objet.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .type(request.getType())
                .statut(StatutObjet.ACTIF)
                .localisation(request.getLocalisation())
                .dateEvenement(request.getDateEvenement())
                .categorie(categorie)
                .proprietaire(proprietaire)
                .build();

        Objet saved = objetRepository.save(objet);

        if (request.getPhotosUrls() != null && !request.getPhotosUrls().isEmpty()) {
            List<Photo> photos = request.getPhotosUrls().stream()
                    .map(url -> Photo.builder().url(url).objet(saved).build())
                    .collect(Collectors.toList());
            saved.setPhotos(photos);
            objetRepository.save(saved);
        }

        return toResponse(saved);
    }

    public Page<ObjetResponse> listerObjets(TypeObjet type, StatutObjet statut, Pageable pageable) {
        if (type != null && statut != null) {
            return objetRepository.findByTypeAndStatut(type, statut, pageable).map(this::toResponse);
        } else if (type != null) {
            return objetRepository.findByType(type, pageable).map(this::toResponse);
        } else if (statut != null) {
            return objetRepository.findByStatut(statut, pageable).map(this::toResponse);
        }
        return objetRepository.findAll(pageable).map(this::toResponse);
    }

    public ObjetResponse getObjetById(Long id) {
        Objet objet = objetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Objet", id));
        return toResponse(objet);
    }

    public Page<ObjetResponse> rechercher(String keyword, TypeObjet type, StatutObjet statut,
                                          Long categorieId, Pageable pageable) {
        boolean sansMotCle = keyword == null || keyword.isBlank();
        Page<Objet> page = sansMotCle
                ? objetRepository.rechercherSansMotCle(type, statut, categorieId, pageable)
                : objetRepository.rechercherAvecMotCle(keyword.trim(), type, statut, categorieId, pageable);
        return page.map(this::toResponse);
    }

    @Transactional
    public ObjetResponse modifierObjet(Long id, ObjetRequest request, String email) {
        Objet objet = objetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Objet", id));

        if (!objet.getProprietaire().getEmail().equals(email)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier cet objet");
        }

        objet.setTitre(request.getTitre());
        objet.setDescription(request.getDescription());
        objet.setType(request.getType());
        objet.setLocalisation(request.getLocalisation());
        objet.setDateEvenement(request.getDateEvenement());

        if (request.getCategorieId() != null) {
            Categorie categorie = categorieRepository.findById(request.getCategorieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categorie", request.getCategorieId()));
            objet.setCategorie(categorie);
        }

        if (request.getPhotosUrls() != null) {
            if (objet.getPhotos() == null) {
                objet.setPhotos(new ArrayList<>());
            } else {
                objet.getPhotos().clear();
            }
            for (String url : request.getPhotosUrls()) {
                objet.getPhotos().add(Photo.builder().url(url).objet(objet).build());
            }
        }

        return toResponse(objetRepository.save(objet));
    }

    @Transactional
    public void supprimerObjet(Long id, String email) {
        Objet objet = objetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Objet", id));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;
        boolean isOwner = objet.getProprietaire().getEmail().equals(email);

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer cet objet");
        }

        objetRepository.delete(objet);
    }

    @Transactional
    public ObjetResponse changerStatut(Long id, StatutObjet statut, String email) {
        Objet objet = objetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Objet", id));

        if (!objet.getProprietaire().getEmail().equals(email)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier le statut de cet objet");
        }

        objet.setStatut(statut);
        return toResponse(objetRepository.save(objet));
    }

    public List<ObjetResponse> getMesObjets(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return objetRepository.findByProprietaireId(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ObjetResponse toResponse(Objet objet) {
        List<String> photosUrls = objet.getPhotos() != null
                ? objet.getPhotos().stream().map(Photo::getUrl).collect(Collectors.toList())
                : List.of();

        return ObjetResponse.builder()
                .id(objet.getId())
                .titre(objet.getTitre())
                .description(objet.getDescription())
                .type(objet.getType())
                .statut(objet.getStatut())
                .localisation(objet.getLocalisation())
                .dateEvenement(objet.getDateEvenement())
                .categorieId(objet.getCategorie() != null ? objet.getCategorie().getId() : null)
                .categorieNom(objet.getCategorie() != null ? objet.getCategorie().getNom() : null)
                .categorieDescription(objet.getCategorie() != null ? objet.getCategorie().getDescription() : null)
                .proprietaireId(objet.getProprietaire().getId())
                .proprietaireUsername(objet.getProprietaire().getUsername())
                .photosUrls(photosUrls)
                .createdAt(objet.getCreatedAt())
                .updatedAt(objet.getUpdatedAt())
                .build();
    }
}