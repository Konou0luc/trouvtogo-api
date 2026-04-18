package com.objetcol.collectobjet.service;

import com.objetcol.collectobjet.dto.request.MessageRequest;
import com.objetcol.collectobjet.dto.response.MessageResponse;
import com.objetcol.collectobjet.exception.ResourceNotFoundException;
import com.objetcol.collectobjet.exception.UnauthorizedException;
import com.objetcol.collectobjet.model.Message;
import com.objetcol.collectobjet.model.Objet;
import com.objetcol.collectobjet.model.User;
import com.objetcol.collectobjet.repository.MessageRepository;
import com.objetcol.collectobjet.repository.ObjetRepository;
import com.objetcol.collectobjet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ObjetRepository objetRepository;

    @Transactional
    public MessageResponse envoyerMessage(MessageRequest request, String emailExpediteur) {
        User expediteur = userRepository.findByEmail(emailExpediteur)
                .orElseThrow(() -> new ResourceNotFoundException("Expéditeur introuvable"));

        // ✅ Corrigé : getDestinataireId() avec I majuscule
        User destinataire = userRepository.findById(request.getDestinataireId())
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire introuvable"));

        if (expediteur.getId().equals(destinataire.getId())) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous envoyer un message à vous-même");
        }

        Objet objet = null;
        if (request.getObjetId() != null) {
            objet = objetRepository.findById(request.getObjetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Objet introuvable"));
        }

        Message message = Message.builder()
                .contenu(request.getContenu())
                .expediteur(expediteur)
                .destinataire(destinataire)
                .objet(objet)
                .lu(false)
                .build();

        return toResponse(messageRepository.save(message));
    }

    public List<MessageResponse> getMessagesRecus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return messageRepository.findByDestinataireIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<MessageResponse> getMessagesEnvoyes(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return messageRepository.findByExpediteurIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<MessageResponse> getConversation(Long autreUserId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return messageRepository.findConversation(user.getId(), autreUserId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void marquerCommeLu(Long messageId, String email) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message introuvable"));

        if (!message.getDestinataire().getEmail().equals(email)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier ce message");
        }

        message.setLu(true);
        messageRepository.save(message);
    }

    public long getNombreMessagesNonLus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return messageRepository.countByDestinataireIdAndLuFalse(user.getId());
    }

    private MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .contenu(message.getContenu())
                .lu(message.isLu())
                .expediteurId(message.getExpediteur().getId())
                .expediteurUsername(message.getExpediteur().getUsername())
                .destinataireId(message.getDestinataire().getId())
                .destinataireUsername(message.getDestinataire().getUsername())
                .objetId(message.getObjet() != null ? message.getObjet().getId() : null)
                .objetTitre(message.getObjet() != null ? message.getObjet().getTitre() : null)
                .createdAt(message.getCreatedAt())
                .build();
    }
}