package com.objetcol.collectobjet.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SmsService {

    @Value("${sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${sms.provider:mock}")
    private String smsProvider;

    @Value("${africastalking.username:sandbox}")
    private String atUsername;

    @Value("${africastalking.api-key:}")
    private String atApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void envoyerSms(String telephone, String message) {
        if (!smsEnabled) {
            log.info("[SMS-MOCK] À: {} | Message: {}", telephone, message);
            return;
        }

        switch (smsProvider.toLowerCase()) {
            case "africastalking" -> envoyerViaAfricasTalking(telephone, message);
            case "orange"         -> envoyerViaOrange(telephone, message);
            default -> log.warn("Fournisseur SMS inconnu : {}", smsProvider);
        }
    }

    public void notifierNouveauMessage(String telephone, String expediteurUsername) {
        String sms = String.format(
                "RetrouveTout: Vous avez reçu un nouveau message de %s. Connectez-vous pour le lire.",
                expediteurUsername
        );
        envoyerSms(telephone, sms);
    }

    public void notifierCorrespondance(String telephone, String titreObjet) {
        String sms = String.format(
                "RetrouveTout: Une correspondance a été trouvée pour votre objet '%s'. Connectez-vous pour voir les détails.",
                titreObjet
        );
        envoyerSms(telephone, sms);
    }

    // ─── Africa's Talking ─────────────────────────────────────────────────────

    private void envoyerViaAfricasTalking(String telephone, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("apiKey", atApiKey);
            headers.set("Accept", "application/json");
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("username", atUsername);
            body.add("to", telephone);
            body.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.africastalking.com/version1/messaging",
                    request,
                    String.class
            );

            log.info("[AFRICASTALKING] SMS envoyé à {} | Statut: {}", telephone, response.getStatusCode());

        } catch (Exception e) {
            log.error("[AFRICASTALKING] Échec envoi SMS à {} : {}", telephone, e.getMessage());
        }
    }

    // ─── Orange ───────────────────────────────────────────────────────────────

    private void envoyerViaOrange(String telephone, String message) {
        // TODO: Intégrer l'API Orange SMS
        log.info("[ORANGE] Envoi SMS à {} : {}", telephone, message);
    }
}