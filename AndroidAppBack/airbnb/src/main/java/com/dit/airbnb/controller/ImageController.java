package com.dit.airbnb.controller;

import com.dit.airbnb.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/app")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/image/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> getImage(@PathVariable(value = "userId") Long userId) throws FileNotFoundException {
        return imageService.getUserImage(userId);
    }

}
