package com.objetcol.collectobjet.service;

import com.objetcol.collectobjet.dto.request.CategorieRequest;
import com.objetcol.collectobjet.dto.response.CategorieResponse;
import com.objetcol.collectobjet.exception.ResourceNotFoundException;
import com.objetcol.collectobjet.model.Categorie;
import com.objetcol.collectobjet.repository.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public List<CategorieResponse> listerToutes() {
        return categorieRepository.findAll(Sort.by("nom")).stream()
                .map(c -> CategorieResponse.builder()
                        .id(c.getId())
                        .nom(c.getNom())
                        .description(c.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    // ========== Méthodes Admin ==========

    @Transactional
    public CategorieResponse creerCategorie(CategorieRequest request) {
        if (categorieRepository.existsByNom(request.getNom())) {
            throw new IllegalArgumentException("Une catégorie avec ce nom existe déjà");
        }

        Categorie categorie = Categorie.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .build();

        Categorie saved = categorieRepository.save(categorie);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public CategorieResponse getCategorieById(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + id));
        return mapToResponse(categorie);
    }

    @Transactional
    public CategorieResponse modifierCategorie(Long id, CategorieRequest request) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + id));

        // Vérifier si le nouveau nom existe déjà (en excluant cette catégorie)
        if (!categorie.getNom().equals(request.getNom()) 
                && categorieRepository.existsByNom(request.getNom())) {
            throw new IllegalArgumentException("Une catégorie avec ce nom existe déjà");
        }

        categorie.setNom(request.getNom());
        categorie.setDescription(request.getDescription());

        Categorie saved = categorieRepository.save(categorie);
        return mapToResponse(saved);
    }

    @Transactional
    public void supprimerCategorie(Long id) {
        if (!categorieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + id);
        }
        categorieRepository.deleteById(id);
    }

    private CategorieResponse mapToResponse(Categorie categorie) {
        return CategorieResponse.builder()
                .id(categorie.getId())
                .nom(categorie.getNom())
                .description(categorie.getDescription())
                .build();
    }
}
