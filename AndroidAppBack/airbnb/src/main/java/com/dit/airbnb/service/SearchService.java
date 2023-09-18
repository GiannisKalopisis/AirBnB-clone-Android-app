package com.dit.airbnb.service;

import com.dit.airbnb.dto.*;
import com.dit.airbnb.exception.ResourceNotFoundException;
import com.dit.airbnb.repository.ApartmentRepository;
import com.dit.airbnb.repository.SearchLogRepository;
import com.dit.airbnb.repository.UserRegRepository;
import com.dit.airbnb.request.search.SearchRequest;
import com.dit.airbnb.response.SearchResponse;
import com.dit.airbnb.response.generic.ApiResponse;
import com.dit.airbnb.response.generic.PagedResponse;
import com.dit.airbnb.security.user.UserDetailsImpl;
import jakarta.persistence.criteria.*;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


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

        Page<Apartment> apartmentPage = searchProducts(searchRequest, page, size);

        PagedResponse<SearchResponse> pagedSearchResponses = createApartmentPageResponse(searchRequest, apartmentPage);

        return ResponseEntity.ok(new ApiResponse(true, "search succeed", pagedSearchResponses));
    }

    private PagedResponse<SearchResponse> createApartmentPageResponse(SearchRequest searchRequest, Page<Apartment> apartmentPage) {
        if (apartmentPage.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), apartmentPage.getNumber(),
                    apartmentPage.getSize(), apartmentPage.getTotalElements(),
                    apartmentPage.getTotalPages(), apartmentPage.isLast());
        }

        List<SearchResponse> searchResponses = new ArrayList<>();
        for (Apartment apartment : apartmentPage) {
            Double totalRating = 0.0;
            Set<Booking> bookings = apartment.getBookings();
            int bookingReviewCard = 0;
            BigDecimal totalCost = BigDecimal.valueOf(0.0);
            // NOTE(geo): MM impl
            if (searchRequest.getNumberOfGuests() != null) {
                for (Booking booking : bookings) {
                    for (BookingReview bookingReview : booking.getBookingReviews()) {
                        totalRating += bookingReview.getRating();
                        bookingReviewCard++;
                    }
                }

                // = minRetailPrice + numOfGuests*extraCostPerPerson)
                BigDecimal extraCostPerPerson = apartment.getExtraCostPerPerson();
                BigDecimal minRetailPrice = apartment.getMinRetailPrice();
                totalCost = extraCostPerPerson != null && minRetailPrice != null ? extraCostPerPerson.multiply(new BigDecimal((searchRequest.getNumberOfGuests()))).add(apartment.getMinRetailPrice()) : BigDecimal.valueOf(0.0);
            }
            searchResponses.add(new SearchResponse(apartment.getId(), totalCost,  bookingReviewCard != 0 ? Precision.round((totalRating / (double) bookingReviewCard), 2) : 0.0,
                    apartment.getCountry(), apartment.getCity(), apartment.getDistrict(), apartment.getDescription(), apartment.getMaxVisitors()));
        }

        // Define a custom comparator based on the totalCost attribute
        Comparator<SearchResponse> totalCostComparator = Comparator.comparing(SearchResponse::getTotalCost);
        searchResponses.sort(totalCostComparator);

        return new PagedResponse<>(searchResponses, apartmentPage.getNumber(),
                apartmentPage.getSize(), apartmentPage.getTotalElements(),
                apartmentPage.getTotalPages(), apartmentPage.isLast());
    }



    public Page<Apartment> searchProducts(SearchRequest searchRequest, int page, int size) {

        validatePageParametersService.validate(page, size);

        return apartmentRepository.findAll(getApartmentSpecification(searchRequest), PageRequest.of(page, size));
    }

    private Specification<Apartment> getApartmentSpecification(SearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            query.distinct(true);

            // Create a join between Apartment and Booking
            Join<Apartment, Booking> bookingJoin = root.join("bookings", JoinType.LEFT);

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
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("availableStartDate"), searchRequest.getAvailableStartDate()));
            }

            if (searchRequest.getAvailableEndDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("availableEndDate"), searchRequest.getAvailableEndDate()));
            }

            if (searchRequest.getRentalType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("rentalType"), searchRequest.getRentalType()));
            }

            if (searchRequest.getNumberOfGuests() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("maxVisitors"), searchRequest.getNumberOfGuests()));
            }

//            if (searchRequest.getAvailableStartDate() != null && searchRequest.getAvailableEndDate() != null) {
//                predicates.add(criteriaBuilder.isTrue(
//                        criteriaBuilder.selectCase()
//                        .when(criteriaBuilder.isNotNull(bookingJoin.get("id")),
//                                criteriaBuilder.equal(criteriaBuilder.count(criteriaBuilder.not(criteriaBuilder.or(
//                                criteriaBuilder.lessThan(bookingJoin.get("checkOutDate"), searchRequest.getAvailableStartDate()),
//                                criteriaBuilder.greaterThan(bookingJoin.get("checkInDate"), searchRequest.getAvailableEndDate()))
//                        )), criteriaBuilder.literal(0))).otherwise(criteriaBuilder.literal(true)).as(Boolean.class)
//                        )
//                );
//            }

            // magic :)
            if (searchRequest.getAvailableStartDate() != null && searchRequest.getAvailableEndDate() != null) {
                // Subquery for checking bookings
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<Booking> subRoot = subquery.from(Booking.class);
                subquery.select(criteriaBuilder.literal(1));
                subquery.where(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(root.get("id"), subRoot.get("apartment").get("id")),
                                criteriaBuilder.greaterThanOrEqualTo(subRoot.get("checkOutDate"), searchRequest.getAvailableStartDate()),
                                criteriaBuilder.lessThanOrEqualTo(subRoot.get("checkInDate"), searchRequest.getAvailableEndDate())
                        )
                );

                predicates.add(criteriaBuilder.not(criteriaBuilder.exists(subquery)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
