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

    public List<com.objetcol.collectobjet.dto.response.ConversationResponse> getConversations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        List<Message> received = messageRepository.findByDestinataireIdOrderByCreatedAtDesc(user.getId());
        List<Message> sent = messageRepository.findByExpediteurIdOrderByCreatedAtDesc(user.getId());

        // aggregate by partner user id
        java.util.Map<Long, java.util.List<Message>> byPartner = new java.util.HashMap<>();

        for (Message m : received) {
            Long partner = m.getExpediteur().getId();
            byPartner.computeIfAbsent(partner, k -> new java.util.ArrayList<>()).add(m);
        }
        for (Message m : sent) {
            Long partner = m.getDestinataire().getId();
            byPartner.computeIfAbsent(partner, k -> new java.util.ArrayList<>()).add(m);
        }

        java.util.List<com.objetcol.collectobjet.dto.response.ConversationResponse> out = new java.util.ArrayList<>();

        for (java.util.Map.Entry<Long, java.util.List<Message>> e : byPartner.entrySet()) {
            Long otherUserId = e.getKey();
            java.util.List<Message> list = e.getValue();

            // find last message
            Message last = list.stream().max(java.util.Comparator.comparing(Message::getCreatedAt)).orElse(null);
            if (last == null) continue;

            int unread = (int) list.stream().filter(m -> !m.isLu() && m.getDestinataire().getId().equals(user.getId())).count();

            com.objetcol.collectobjet.dto.response.ConversationResponse.LastMessageResponse lastMsg =
                    com.objetcol.collectobjet.dto.response.ConversationResponse.LastMessageResponse.builder()
                            .content(last.getContenu())
                            .createdAt(last.getCreatedAt())
                            .isFromMe(last.getExpediteur().getId().equals(user.getId()))
                            .build();

            com.objetcol.collectobjet.dto.response.ConversationResponse.UserPublicResponse otherUser =
                    com.objetcol.collectobjet.dto.response.ConversationResponse.UserPublicResponse.builder()
                            .id(otherUserId)
                            .name(list.get(0).getExpediteur().getId().equals(otherUserId) ? list.get(0).getExpediteur().getUsername() : list.get(0).getDestinataire().getUsername())
                            .avatarUrl(null)
                            .build();

            com.objetcol.collectobjet.dto.response.ConversationResponse conv =
                    com.objetcol.collectobjet.dto.response.ConversationResponse.builder()
                            .id(otherUserId)
                            .otherUserId(otherUserId)
                            .otherUser(otherUser)
                            .itemId(last.getObjet() != null ? last.getObjet().getId() : null)
                            .item(last.getObjet() != null ? com.objetcol.collectobjet.dto.response.ObjetResponse.builder()
                                    .id(last.getObjet().getId())
                                    .titre(last.getObjet().getTitre())
                                    .build() : null)
                            .lastMessage(lastMsg)
                            .unreadCount(unread)
                            .matchScore(0)
                            .updatedAt(last.getCreatedAt())
                            .build();

            out.add(conv);
        }

        // sort by updatedAt desc
        out.sort((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()));
        return out;
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