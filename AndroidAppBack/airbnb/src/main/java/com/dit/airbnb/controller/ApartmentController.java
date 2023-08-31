package com.dit.airbnb.controller;

import com.dit.airbnb.request.apartment.ApartmentRequest;
import com.dit.airbnb.request.user_reg.UserRegUpdateRequest;
import com.dit.airbnb.security.user.CurrentUser;
import com.dit.airbnb.security.user.UserDetailsImpl;
import com.dit.airbnb.service.ApartmentService;
import com.dit.airbnb.util.PaginationConstants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
public class ApartmentController {

    @Autowired
    private ApartmentService apartmentService;

    @PostMapping( "/apartment")
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    public ResponseEntity<?> createApartment(@Valid @RequestBody ApartmentRequest apartmentRequest,
                                             @Valid @CurrentUser UserDetailsImpl currentUser) {
        return apartmentService.createApartment(currentUser, apartmentRequest);
    }

    @PutMapping("/apartment/{apartmentId}")
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    public ResponseEntity<?> updateApartment(@PathVariable(value = "apartmentId") Long apartmentId,
                                             @Valid @RequestBody ApartmentRequest apartmentRequest,
                                             @Valid @CurrentUser UserDetailsImpl currentUser) {
        return apartmentService.updateApartmentById(apartmentId, currentUser, apartmentRequest);
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
}
