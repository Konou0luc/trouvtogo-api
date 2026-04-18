package com.objetcol.collectobjet.service;

import com.objetcol.collectobjet.dto.response.CategorieResponse;
import com.objetcol.collectobjet.repository.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
}
