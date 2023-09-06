package com.dit.airbnb.dto;

import com.dit.airbnb.dto.enums.RentalType;
import com.dit.airbnb.request.search.SearchRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;
import java.util.Date;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "search_log")
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // external tables
    @Getter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_reg_id")
    private UserReg userReg;

    @CreatedDate
    @Column(name = "timeLog")
    private Timestamp timeLog;

    @Column(name = "district")
    private String district;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "availableStartDate")
    private Date availableStartDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "availableEndDate")
    private Date availableEndDate;

    @Column(name = "rentalType")
    @Enumerated
    private RentalType rentalType;

    @Column(name = "numberOfGuests")
    private Integer numberOfGuests;

    public SearchLog(SearchRequest searchRequest) {
        this.district = searchRequest.getDistrict();
        this.city = searchRequest.getCity();
        this.country = searchRequest.getCountry();
        this.numberOfGuests = searchRequest.getNumberOfGuests();
        this.availableStartDate = searchRequest.getAvailableStartDate();
        this.availableEndDate = searchRequest.getAvailableEndDate();
        this.rentalType = searchRequest.getRentalType();
    }

}
