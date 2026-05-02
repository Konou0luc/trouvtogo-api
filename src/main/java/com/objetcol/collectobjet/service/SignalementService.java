package com.objetcol.collectobjet.service;

import com.objetcol.collectobjet.dto.request.SignalementRequest;
import com.objetcol.collectobjet.dto.response.SignalementResponse;
import com.objetcol.collectobjet.exception.ResourceNotFoundException;
import com.objetcol.collectobjet.model.Objet;
import com.objetcol.collectobjet.model.Signalement;
import com.objetcol.collectobjet.model.User;
import com.objetcol.collectobjet.repository.ObjetRepository;
import com.objetcol.collectobjet.repository.SignalementRepository;
import com.objetcol.collectobjet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignalementService {

    private final SignalementRepository signalementRepository;
    private final ObjetRepository objetRepository;
    private final UserRepository userRepository;

    @Transactional
    public SignalementResponse createSignalement(Long objetId, String reporterEmail, SignalementRequest request) {
        Objet objet = objetRepository.findById(objetId)
                .orElseThrow(() -> new ResourceNotFoundException("Objet", objetId));

        User reporter = userRepository.findByEmailIgnoreCase(reporterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        Signalement s = Signalement.builder()
                .objet(objet)
                .reporter(reporter)
                .message(request.getMessage())
                .build();

        Signalement saved = signalementRepository.save(s);

        return toResponse(saved);
    }

    public List<SignalementResponse> listAll() {
        return signalementRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<SignalementResponse> listUnresolved() {
        return signalementRepository.findByResolvedFalse().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public SignalementResponse resolve(Long id, String resolverEmail) {
        Signalement s = signalementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Signalement", id));

        User resolver = userRepository.findByEmailIgnoreCase(resolverEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        s.setResolved(true);
        s.setResolver(resolver);
        s.setResolvedAt(LocalDateTime.now());

        Signalement saved = signalementRepository.save(s);
        return toResponse(saved);
    }

    private SignalementResponse toResponse(Signalement s) {
        return SignalementResponse.builder()
                .id(s.getId())
                .objetId(s.getObjet().getId())
                .objetTitre(s.getObjet().getTitre())
                .reporterId(s.getReporter().getId())
                .reporterUsername(s.getReporter().getUsername())
                .message(s.getMessage())
                .resolved(s.isResolved())
                .resolverId(s.getResolver() != null ? s.getResolver().getId() : null)
                .resolverUsername(s.getResolver() != null ? s.getResolver().getUsername() : null)
                .createdAt(s.getCreatedAt())
                .resolvedAt(s.getResolvedAt())
                .build();
    }
}
