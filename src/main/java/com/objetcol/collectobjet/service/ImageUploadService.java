package com.objetcol.collectobjet.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private static final Logger log = LoggerFactory.getLogger(ImageUploadService.class);

    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    public List<String> uploadImages(MultipartFile[] files) {
        if (!StringUtils.hasText(cloudName)
                || !StringUtils.hasText(apiKey)
                || !StringUtils.hasText(apiSecret)) {
            throw new IllegalStateException(
                    "Cloudinary n'est pas configuré. Définissez CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY et CLOUDINARY_API_SECRET sur le serveur.");
        }
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("Aucun fichier fourni");
        }
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Seules les images sont acceptées : " + file.getOriginalFilename());
            }
            Map<String, Object> result;
            try {
                byte[] payload = file.getBytes();
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = cloudinary.uploader().upload(
                        payload,
                        ObjectUtils.asMap(
                                "folder", "collectobjet",
                                "resource_type", "image"
                        ));
                result = uploadResult;
            } catch (IOException e) {
                log.warn("Échec téléversement Cloudinary (réseau ou fichier) : {}", e.toString());
                throw new IllegalStateException(
                        "Impossible d'enregistrer l'image (erreur réseau ou stockage). Réessayez plus tard.", e);
            } catch (RuntimeException e) {
                log.warn("Échec téléversement Cloudinary : {}", e.toString());
                throw new IllegalStateException(
                        "Impossible d'enregistrer l'image. Vérifiez les identifiants Cloudinary (CLOUDINARY_*) sur le serveur.",
                        e);
            }
            Object secureUrl = result.get("secure_url");
            if (secureUrl == null) {
                secureUrl = result.get("url");
            }
            if (secureUrl == null) {
                throw new IllegalStateException("Réponse du stockage d'images invalide (pas d'URL).");
            }
            urls.add(secureUrl.toString());
        }
        if (urls.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier valide à envoyer");
        }
        return urls;
    }
}
