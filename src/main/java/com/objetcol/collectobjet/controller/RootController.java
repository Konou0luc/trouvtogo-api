package com.objetcol.collectobjet.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String root() {
        return "CollectObjet API — service en ligne. Documentation : /swagger-ui/index.html";
    }
}
