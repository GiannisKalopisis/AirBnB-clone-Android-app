package com.dit.airbnb.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "recommendation")
public class RecommendationParameters {

    private Integer maxApartmentsToLoad;

    private Integer maxReviewsToLoad;

    // vector size
    private Integer K;

    // learning rate
    private Double H;

    private String ratingFunction;

    private String vectorInitializer;

    private Double normalFactor;

}
