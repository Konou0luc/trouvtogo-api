package com.objetcol.collectobjet.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean Cloudinary si la clé {@code cloudinary.api-key} est présente dans la configuration.
 * Les valeurs peuvent être vides (variables d'environnement non définies) : la validation
 * complète (cloud name, API key, secret) est faite dans {@link com.objetcol.collectobjet.service.ImageUploadService}.
 */
@Configuration
@ConditionalOnProperty(prefix = "cloudinary", name = "api-key", matchIfMissing = false)
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret) {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }
}
