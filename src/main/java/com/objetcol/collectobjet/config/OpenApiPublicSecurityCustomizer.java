package com.objetcol.collectobjet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Les routes publiques (permitAll) n'exigent pas de JWT dans la doc OpenAPI,
 * malgré le {@link io.swagger.v3.oas.models.security.SecurityRequirement} global.
 */
@Component
public class OpenApiPublicSecurityCustomizer implements OpenApiCustomizer {

    @Override
    public void customise(OpenAPI openApi) {
        if (openApi.getPaths() == null) {
            return;
        }
        openApi.getPaths().forEach((path, pathItem) ->
                pathItem.readOperationsMap().forEach((method, operation) -> {
                    if (isPublic(path, method)) {
                        operation.setSecurity(Collections.emptyList());
                    }
                }));
    }

    private static boolean isPublic(String path, PathItem.HttpMethod method) {
        if (path.startsWith("/api/auth")) {
            return true;
        }
        if ("/api/categories".equals(path) && method == PathItem.HttpMethod.GET) {
            return true;
        }
        if ("/api/objets".equals(path) && method == PathItem.HttpMethod.GET) {
            return true;
        }
        if ("/api/objets/recherche".equals(path) && method == PathItem.HttpMethod.GET) {
            return true;
        }
        if ("/api/stats/communaute".equals(path) && method == PathItem.HttpMethod.GET) {
            return true;
        }
        if ("/api/objets/{id}".equals(path) && method == PathItem.HttpMethod.GET) {
            return true;
        }
        return false;
    }
}
