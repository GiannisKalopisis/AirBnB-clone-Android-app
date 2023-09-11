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
import java.io.IOException;

@RestController
@RequestMapping("/app")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/image/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> getUserRegImage(@PathVariable(value = "userId") Long userId) throws FileNotFoundException {
        return imageService.getUserImage(userId);
    }

    @GetMapping("/image/apartment/{imageId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> getApartmentImageByImageId(@PathVariable(value = "imageId") Long imageId) throws IOException {
        return imageService.getApartmentImageByImageId(imageId);
    }

    @GetMapping("/image/{apartmentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> getApartmentImageIds(@PathVariable(value = "apartmentId") Long apartmentId) throws IOException {
        return imageService.getApartmentImageIds(apartmentId);
    }
}
