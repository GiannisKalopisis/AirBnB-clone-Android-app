package com.dit.airbnb.controller;

import com.dit.airbnb.request.apartment.ApartmentRequest;
import com.dit.airbnb.request.user_reg.SignUpRequest;
import com.dit.airbnb.request.user_reg.UserRegUpdateRequest;
import com.dit.airbnb.security.user.CurrentUser;
import com.dit.airbnb.security.user.UserDetailsImpl;
import com.dit.airbnb.service.ApartmentService;
import com.dit.airbnb.util.PaginationConstants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/app")
public class ApartmentController {

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping( "/apartment")
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    public ResponseEntity<?> createApartment(@Valid @RequestParam(value = "apartmentRequest") String apartmentRequest,
                                             @RequestParam(value = "image") List<MultipartFile> image,
                                             @Valid @CurrentUser UserDetailsImpl currentUser)
        throws JsonParseException, JsonMappingException, IOException {
        ApartmentRequest apartmentRequestReal = objectMapper.readValue(apartmentRequest, ApartmentRequest.class);
        return apartmentService.createApartment(currentUser, apartmentRequestReal, image);
    }

    @PutMapping("/apartment/image/{apartmentId}")
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    public ResponseEntity<?> updateApartmentWithImages(@PathVariable(value = "apartmentId") Long apartmentId,
                                                     @Valid @RequestParam(value = "apartmentRequest") String apartmentRequest,
                                                     @RequestParam(value = "image") List<MultipartFile> image,
                                                     @Valid @CurrentUser UserDetailsImpl currentUser)
            throws JsonParseException, JsonMappingException, IOException {
        ApartmentRequest apartmentRequestReal = objectMapper.readValue(apartmentRequest, ApartmentRequest.class);
        return apartmentService.updateApartmentById(apartmentId, currentUser, apartmentRequestReal, image);
    }

    @PutMapping("/apartment/{apartmentId}")
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    public ResponseEntity<?> updateApartment(@PathVariable(value = "apartmentId") Long apartmentId,
                                             @Valid @RequestParam(value = "apartmentRequest") String apartmentRequest,
                                             @Valid @CurrentUser UserDetailsImpl currentUser)
        throws JsonParseException, JsonMappingException, IOException {
        ApartmentRequest apartmentRequestReal = objectMapper.readValue(apartmentRequest, ApartmentRequest.class);
        return apartmentService.updateApartmentById(apartmentId, currentUser, apartmentRequestReal);
    }

    @GetMapping("/apartment/{apartmentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> getApartmentInfo(@PathVariable(value = "apartmentId") Long apartmentId,
                                              @Valid @CurrentUser UserDetailsImpl currentUser) {
        return apartmentService.getApartmentById(apartmentId, currentUser);
    }

    @DeleteMapping("/apartment/{apartmentId}")
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    public ResponseEntity<?> deleteApartment(@PathVariable(value = "apartmentId") Long apartmentId,
                                             @Valid @CurrentUser UserDetailsImpl currentUser) {
        return apartmentService.deleteApartmentById(apartmentId, currentUser);
    }

    @GetMapping("/apartment/{apartmentId}/host")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> getHostId(@PathVariable(value = "apartmentId") Long apartmentId,
                                       @Valid @CurrentUser UserDetailsImpl currentUser) {
        return apartmentService.getHostIdByApartmentId(apartmentId);
    }


    @GetMapping(value = "/apartment/{hostId}", params = {"page", "size"})
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    public ResponseEntity<?> getHostApartments(
                                        @RequestParam(value = "page", defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
                                        @RequestParam(value = "size", defaultValue = PaginationConstants.DEFAULT_SIZE) int size,
                                        @PathVariable(value = "hostId") Long hostId,
                                        @Valid @CurrentUser UserDetailsImpl currentUser) {
        return apartmentService.getHostApartments(hostId, page, size);
    }

    @GetMapping("/apartment/rec/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> getRecommendedApartments( @PathVariable(value = "userId") Long userId) {
        return apartmentService.recommendApartment(userId);
    }

}
