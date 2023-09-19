package com.dit.airbnb.controller;

import com.dit.airbnb.request.search.SearchRequest;
import com.dit.airbnb.security.user.CurrentUser;
import com.dit.airbnb.security.user.UserDetailsImpl;
import com.dit.airbnb.service.SearchService;
import com.dit.airbnb.util.PaginationConstants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping(path = "/search", params = {"page", "size"})
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> search(@RequestParam(value = "page", defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
                                    @RequestParam(value = "size", defaultValue = PaginationConstants.DEFAULT_SIZE) int size,
                                    @Valid @RequestBody SearchRequest searchRequest,
                                    @Valid @CurrentUser UserDetailsImpl currentUser) {
        return searchService.searchApartment(currentUser, searchRequest, page, size);
    }


}
