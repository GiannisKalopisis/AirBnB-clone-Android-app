package com.dit.airbnb.service;

import com.dit.airbnb.dto.Apartment;
import com.dit.airbnb.dto.Booking;
import com.dit.airbnb.dto.BookingReview;
import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.repository.ApartmentRepository;
import com.dit.airbnb.repository.BookingReviewRepository;
import com.dit.airbnb.repository.UserRegRepository;
import com.dit.airbnb.response.SearchResponse;
import com.dit.airbnb.util.MaxMinApartmentValues;
import com.dit.airbnb.util.rating_function.DirectRatingFunction;
import com.dit.airbnb.util.rating_function.LogWeightedRatingFunction;
import com.dit.airbnb.util.rating_function.RatingFunction;
import com.dit.airbnb.util.RecommendationParameters;
import com.dit.airbnb.util.vector_init.NormalInitializer;
import com.dit.airbnb.util.vector_init.UniformInitializer;
import com.dit.airbnb.util.vector_init.VectorInitializer;
import jakarta.transaction.Transactional;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

// MatrixFactorization
@Service
public class RecommendationService {

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private UserRegRepository userRegRepository;

    @Autowired
    private BookingReviewRepository bookingReviewRepository;

    @Autowired
    private RecommendationParameters recommendationParameters;

    public final static String DEFAULT_RATING_FUNCTION = "default";

    public final static String NORMAL_VECTOR_INITIALIZER = "normal";
    public final static String UNIFORM_VECTOR_INITIALIZER = "uniform";

    public final static int SCALE = 8;

    static RatingFunction ratingFunction = new DirectRatingFunction();
    static RatingFunction logWeightedRatingFunction = new LogWeightedRatingFunction();

    private double calculateFinalPrediction(double[][] V, double[][] F, int i, int j) {
        double prediction = 0.0;
        for (int k = 0; k < recommendationParameters.getK(); k++) {
            prediction += Precision.round(V[i][k] * F[k][j], SCALE);
        }
        return prediction;
    }

    private double[][] multVF(int Vrow, int Vcol, int Frow, int Fcol, double[][] V, double[][] F) {
        double[][] result = new double[Vrow][Fcol];
        for (int i = 0; i < Vrow; i++) {
            for (int j = 0; j < Fcol; j++) {
                double sum = 0.0;
                for (int k = 0; k < Vcol; k++) {
                    // TODO(geo): implement it cache friendly
                    sum += Precision.round(V[i][k] * F[k][j], SCALE);
                }
                result[i][j] = sum;
            }
        }
        return result;
    }

    @Transactional
    public List<SearchResponse> recommend(Long userId) {

        // Set the learning rate (η) to a small positive value
        Double H = recommendationParameters.getH();
        Integer K = recommendationParameters.getK();
        int maxIters = 20;
        double minRMSE = 0.0001;
        double prevRMSE = Double.MAX_VALUE;

        List<UserReg> userRegs = userRegRepository.findAll();

        List<Apartment> apartments = apartmentRepository.findAll();

        final int numberOfUsers = (int) userRegRepository.count();
        final int numberOfItems = (int) apartmentRepository.count();
        double[][] inputArrayX = new double[numberOfUsers][numberOfItems];
        for (int i = 0; i < numberOfUsers; i++) {
            for (int j = 0; j < numberOfItems; j++) {
                inputArrayX[i][j] = 0.0;
            }
        }

        Map<Long, Integer> indexMapper = new HashMap<>();
        Integer indexCount = 0;
        MaxMinApartmentValues maxMinApartmentValues = new MaxMinApartmentValues();

        for (var apartment : apartments) {
            maxMinApartmentValues.compMaxVisitorsValue(apartment.getMaxVisitors());
            maxMinApartmentValues.compMinVisitorsValue(apartment.getMaxVisitors());
            maxMinApartmentValues.compMaxExtraCostPerPersonValue(apartment.getExtraCostPerPerson().doubleValue());
            maxMinApartmentValues.compMinExtraCostPerPersonValue(apartment.getExtraCostPerPerson().doubleValue());
            maxMinApartmentValues.compMaxRetailPriceValue(apartment.getMinRetailPrice().doubleValue());
            maxMinApartmentValues.compMinRetailPriceValue(apartment.getMinRetailPrice().doubleValue());
            Short numberOfBedrooms = apartment.getNumberOfBedrooms();
            Short numberOfBeds = apartment.getNumberOfBeds();
            Short numberOfBathrooms = apartment.getNumberOfBathrooms();
            Short numberOfLivingRooms =  apartment.getNumberOfLivingRooms();
            double numOfPlaces = ( (numberOfBedrooms != null ? numberOfBedrooms : 0) + (numberOfBeds != null ? numberOfBeds : 0) + (numberOfBathrooms != null ? numberOfBathrooms : 0) + (numberOfLivingRooms != null ? numberOfLivingRooms : 0) ) / 4.0;
            maxMinApartmentValues.compMaxNumberOfPlacesValue(numOfPlaces);
            maxMinApartmentValues.compMinNumberOfPlacesValue(numOfPlaces);
            indexMapper.put(apartment.getId(), indexCount);
            indexCount++;
        }

        int currentUserIndex = -1;
        for (int i = 0; i < numberOfUsers; i++) {
            UserReg currentUserReg = userRegs.get(i);
            if (currentUserReg.getId().equals(userId)) currentUserIndex = i;
            Set<Booking> bookings = currentUserReg.getBookings();
            if (bookings == null || bookings.isEmpty()) {
                for (Apartment apartment : currentUserReg.getApartmentLogs()) {
                    inputArrayX[i][indexMapper.get(apartment.getId())] = logWeightedRatingFunction.getRate(apartment, null, maxMinApartmentValues);
                }
                continue;
            }
            boolean hasBooking = false;
            for (Booking booking : bookings) {
                for (BookingReview bookingReview : booking.getBookingReviews()) {
                    // maybe map here
                    if (bookingReview.getCreatorUserReg().getId().equals(currentUserReg.getId())) {
                        hasBooking = true;
                        inputArrayX[i][indexMapper.get(booking.getApartment().getId())] = ratingFunction.getRate(bookingReview);
                    }
                }
            }
            if (!hasBooking) {
                for (Apartment apartment : currentUserReg.getApartmentLogs()) {
                    inputArrayX[i][indexMapper.get(apartment.getId())] = logWeightedRatingFunction.getRate(apartment, null, maxMinApartmentValues);
                }
            }
        }

        // init F,V
        VectorInitializer vectorInitializer;
        if (recommendationParameters.getVectorInitializer().equals(NORMAL_VECTOR_INITIALIZER)) {
            vectorInitializer = new NormalInitializer(recommendationParameters.getNormalFactor());
        } else if (recommendationParameters.getVectorInitializer().equals(UNIFORM_VECTOR_INITIALIZER)) {
            vectorInitializer = new UniformInitializer(0.0, 1.0);
        } else {
            vectorInitializer = new NormalInitializer(recommendationParameters.getNormalFactor());
        }
        Pair<double[][], double[][]> vectors = vectorInitializer.init(numberOfUsers, numberOfItems, K);
        double[][] V = vectors.getFirst();
        double[][] F = vectors.getSecond();


        // Stochastic Gradient Descent (SGD) for matrix factorization
        for (int iter = 0; iter < maxIters; iter++) {
            double rmse = 0.0;
            for (int i = 0; i < numberOfUsers; i++) {
                for (int j = 0; j < numberOfItems; j++) {
                    if (inputArrayX[i][j] > 0) {
                        double prediction = Precision.round(calculateFinalPrediction(V, F, i, j), SCALE);
                        // (eij)
                        double error = Precision.round(inputArrayX[i][j] - prediction, SCALE);
                        // Compute the gradients of eij with respect to V and F
                        for (int k = 0; k < K; k++) {
                            Double gradientF = -2.0 * Precision.round(error * V[i][k], SCALE);
                            Double gradientV = -2.0 * Precision.round(error * F[k][j], SCALE);
                            // update V and F
                            V[i][k] -= Precision.round(H * gradientV,SCALE);
                            F[k][j] -= Precision.round(H  * gradientF, SCALE);
                        }
                        // RMSE
                        rmse += (error * error);
                    }
                }
            }
            rmse = Math.sqrt(rmse / (numberOfUsers * numberOfItems));
            if (Math.abs(prevRMSE - rmse) < minRMSE) {
                break;
            }
            prevRMSE = rmse;
        }

        // Calculate Xpredicted = VF to predict unknown ratings
        double[][] Xpredicted = multVF(numberOfUsers, K, K, numberOfItems, V, F);

        // Return the top 5 rated unknown items per user
        int topN = 5;
        List<SearchResponse> apartmentResponsesTopN = new ArrayList<>(topN);
        Set<Integer> topNIndex = new HashSet<>();
        for (int n = 0; n < topN; n++) {
            int currMaxIndex = -1;
            double currMaxVal = Double.MIN_VALUE;
            for (int j = 0; j < numberOfItems; j++) {
                if (topNIndex.contains(j)) continue;
                double currXpred = Xpredicted[currentUserIndex][j];
                if (Double.compare(currXpred, currMaxVal) > 0) {
                    currMaxVal = currXpred;
                    currMaxIndex = j;
                }
            }
            if (currMaxIndex != -1) topNIndex.add(currMaxIndex);
            else continue;

            // apartment
            Apartment apartment = apartments.get(currMaxIndex);

            Double totalRating = 0.0;
            Set<Booking> bookings = apartment.getBookings();
            int bookingReviewCard = 0;
            BigDecimal totalCost;
            for (Booking booking : bookings) {
                for (BookingReview bookingReview : booking.getBookingReviews()) {
                    totalRating += bookingReview.getRating();
                    bookingReviewCard++;
                }
            }
            // = minRetailPrice + numOfGuests*extraCostPerPerson)
            BigDecimal extraCostPerPerson = apartment.getExtraCostPerPerson();
            // Convert Date objects to LocalDate
            LocalDate localDate1 = apartment.getAvailableStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate localDate2 = apartment.getAvailableEndDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            long dayDifference = ChronoUnit.DAYS.between(localDate1, localDate2);
            BigDecimal minRetailPrice = apartment.getMinRetailPrice() != null ? apartment.getMinRetailPrice().multiply(new BigDecimal(dayDifference)) : null;
            totalCost = extraCostPerPerson != null && minRetailPrice != null ? extraCostPerPerson.multiply(new BigDecimal(2)).add(apartment.getMinRetailPrice()) : BigDecimal.valueOf(0.0);
            apartmentResponsesTopN.add(new SearchResponse(apartment.getId(), totalCost,  bookingReviewCard != 0 ? Precision.round((totalRating / (double) bookingReviewCard), 2) : 0.0,
                    apartment.getCountry(), apartment.getCity(), apartment.getDistrict(), apartment.getDescription(), apartment.getMaxVisitors()));
        }

        return apartmentResponsesTopN;
    }



}
