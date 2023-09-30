package com.dit.airbnb.util.vector_init;

import com.dit.airbnb.service.RecommendationService;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.util.Precision;
import org.springframework.security.core.parameters.P;

import java.util.Random;

public class NormalInitializer implements VectorInitializer {

    public Double normalFactor;

    public NormalInitializer(Double normalFactor) {
        this.normalFactor = normalFactor;
    }

    @Override
    public Pair<double[][], double[][]> init(int numberOfUsers, int numberOfItems, int K) {
        Random random = new Random();
        double[][] V = new double[numberOfUsers][K];
        double[][] F = new double[K][numberOfItems];

        for (int i = 0; i < numberOfUsers; i++) {
            for (int j = 0; j < K; j++) {
                V[i][j] = Precision.round(random.nextGaussian() * normalFactor, RecommendationService.SCALE);
            }
        }
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < numberOfItems; j++) {
                F[i][j] = Precision.round(random.nextGaussian() * normalFactor, RecommendationService.SCALE);
            }
        }
        return new Pair<>(V, F);
    }
}
