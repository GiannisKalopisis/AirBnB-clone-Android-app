package com.dit.airbnb.service;

import com.dit.airbnb.dto.Apartment;
import com.dit.airbnb.dto.SearchLog;
import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.exception.ResourceNotFoundException;
import com.dit.airbnb.repository.ApartmentRepository;
import com.dit.airbnb.repository.SearchLogRepository;
import com.dit.airbnb.repository.UserRegRepository;
import com.dit.airbnb.request.search.SearchRequest;
import com.dit.airbnb.security.user.UserDetailsImpl;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class SearchService {

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private ValidatePageParametersService validatePageParametersService;

    @Autowired
    private UserRegRepository userRegRepository;

    @Autowired
    private SearchLogRepository searchLogRepository;

    public ResponseEntity<?> searchApartment(UserDetailsImpl user, SearchRequest searchRequest, int page, int size) {

        UserReg userReg = userRegRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", user.getId()));

        SearchLog searchLog = new SearchLog(searchRequest);
        searchLog.setUserReg(userReg);
        searchLogRepository.save(searchLog);

        return ResponseEntity.ok(searchProducts(searchRequest, page, size));
    }

    public Page<Apartment> searchProducts(SearchRequest searchRequest, int page, int size) {

        validatePageParametersService.validate(page, size);

        return apartmentRepository.findAll(getApartmentSpecification(searchRequest), PageRequest.of(page, size));
    }

    private Specification<Apartment> getApartmentSpecification(SearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchRequest.getDistrict() != null) {
                predicates.add(criteriaBuilder.equal(root.get("district"), searchRequest.getDistrict()));
            }

            if (searchRequest.getCity() != null) {
                predicates.add(criteriaBuilder.equal(root.get("city"), searchRequest.getCity()));
            }

            if (searchRequest.getCountry() != null) {
                predicates.add(criteriaBuilder.equal(root.get("country"), searchRequest.getCountry()));
            }

            if (searchRequest.getAvailableStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("availableStartDate"), searchRequest.getAvailableStartDate()));
            }

            if (searchRequest.getAvailableEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("availableEndDate"), searchRequest.getAvailableEndDate()));
            }

            if (searchRequest.getRentalType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("rentalType"), searchRequest.getRentalType()));
            }

            if (searchRequest.getNumberOfGuests() != null) {
                predicates.add(criteriaBuilder.equal(root.get("numberOfGuests"), searchRequest.getNumberOfGuests()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
