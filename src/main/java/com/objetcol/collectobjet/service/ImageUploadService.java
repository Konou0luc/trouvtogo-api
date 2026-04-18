package com.objetcol.collectobjet.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
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

    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    public List<String> uploadImages(MultipartFile[] files) throws IOException {
        if (!StringUtils.hasText(cloudName)) {
            throw new IllegalStateException(
                    "Cloudinary n'est pas configuré. Définissez CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY et CLOUDINARY_API_SECRET.");
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
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "collectobjet",
                            "resource_type", "image"
                    ));
            Object secureUrl = result.get("secure_url");
            if (secureUrl == null) {
                secureUrl = result.get("url");
            }
            if (secureUrl == null) {
                throw new IOException("Réponse Cloudinary sans URL");
            }
            urls.add(secureUrl.toString());
        }
        if (urls.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier valide à envoyer");
        }
        return urls;
    }
}
